// Created 2021/10/9 15:06

package cn.com.yting.kxy.web.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Azige
 */
@Component
public class AppleApi {

    private static final Logger LOG = LoggerFactory.getLogger(AppleApi.class);

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String teamId;
    private final String clientId;
    private final String keyId;
    private final ECPublicKey publicKey;
    private final ECPrivateKey privateKey;

    public AppleApi(
        ObjectMapper objectMapper,
        ClientHttpRequestFactory requestFactory,
        @Value("${kxy.web.apple.teamId}") String teamId,
        @Value("${kxy.web.apple.clientId}") String clientId,
        @Value("${kxy.web.apple.keyId}") String keyId
    ) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate(requestFactory);
        this.teamId = teamId;
        this.clientId = clientId;
        this.keyId = keyId;

        try (
            InputStream publicKeyInput = getClass().getResourceAsStream("public_key");
            InputStream privateKeyInput = getClass().getResourceAsStream("private_key")
        ) {
            KeyFactory kf = KeyFactory.getInstance("EC");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(StreamUtils.copyToByteArray(publicKeyInput));
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(StreamUtils.copyToByteArray(privateKeyInput));
            this.publicKey = (ECPublicKey) kf.generatePublic(publicKeySpec);
            this.privateKey = (ECPrivateKey) kf.generatePrivate(privateKeySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    // https://developer.apple.com/documentation/sign_in_with_apple/fetch_apple_s_public_key_for_verifying_token_signature
    public JWKSet getPublicKeys() {
        return restTemplate.getForObject("https://appleid.apple.com/auth/keys", JWKSet.class);
    }

    // https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
    public TokenResponse generateToken(
        String code
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", createClientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        HttpEntity<?> request = new HttpEntity<>(form, headers);
        TokenResponse response = restTemplate.postForObject("https://appleid.apple.com/auth/token", request, TokenResponse.class);
        if (response != null) {
            LOG.debug("获得了 token：{}", response.getId_token());
        } else {
            LOG.debug("generateToken 响应为 null");
        }
        return response;
    }

    public String createClientSecret() {
        return JWT.create()
            .withKeyId(keyId)
            .withIssuer(teamId)
            .withIssuedAt(new Date())
            .withExpiresAt(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
            .withAudience("https://appleid.apple.com")
            .withSubject(clientId)
            .sign(Algorithm.ECDSA256(publicKey, privateKey));
    }
}
