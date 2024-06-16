// Created 2021/8/24 11:06

package cn.com.yting.kxy.web.taptap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Random;

/**
 * @author Azige
 */
@Component
public class TapTapApi {
    private static final Logger LOG = LoggerFactory.getLogger(TapTapApi.class);

    private final String clientId;
    private final RestTemplate restTemplate;
    private final URL userInfoUrl;
    private final ObjectMapper objectMapper;

    private final Random random = new SecureRandom();

    public TapTapApi(
        ClientHttpRequestFactory requestFactory,
        @Value("${kxy.web.taptap.clientId}") String clientId,
        ObjectMapper objectMapper
    ) {
        this.clientId = clientId;
        this.objectMapper = objectMapper;
        restTemplate = new RestTemplate(requestFactory);
        try {
            userInfoUrl = new URL("https://openapi.taptap.com/account/profile/v1?client_id=" + clientId);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    public UserInfo getUserInfo(String accessToken, String macKey) {
        try {
            String authorizationHeader = getAuthorization(userInfoUrl.toString(), "GET", accessToken, macKey);
            RequestEntity<?> request = RequestEntity.get(userInfoUrl.toURI())
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .build();
            LOG.debug("请求的 URL：{}，AUTHORIZATION: {}", userInfoUrl, authorizationHeader);
            ResponseEntity<byte[]> response = restTemplate.exchange(request, byte[].class);
            byte[] body = response.getBody();
            if (body != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.path("success").asBoolean()) {
                    UserInfo userInfo = objectMapper.treeToValue(root.path("data"), UserInfo.class);
                    if (userInfo == null) {
                        LOG.warn("远端响应成功消息但是 data 为 null：{}", new String(response.getBody(), StandardCharsets.UTF_8));
                    }
                    return userInfo;
                } else {
                    LOG.warn("远端没有响应成功消息：{}", new String(response.getBody(), StandardCharsets.UTF_8));
                    return null;
                }
            } else {
                LOG.warn("远端没有响应体，状态码为：{}", response.getStatusCode().value());
                return null;
            }
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(e);
        } catch (HttpClientErrorException e) {
            LOG.debug("远端响应：{}", e.getResponseBodyAsString());
            if (e.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                return null;
            } else {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @param request_url
     * @param method      "GET" or "POST"
     * @param key_id      key id by OAuth 2.0
     * @return authorization string
     */
    private String getAuthorization(String request_url, String method, String key_id, String mac_key) {
        try {
            URL url = new URL(request_url);
            String time = String.format(Locale.US, "%010d", System.currentTimeMillis() / 1000);
            String randomStr = getRandomString();
            String host = url.getHost();
            String uri = request_url.substring(request_url.lastIndexOf(host) + host.length());
            String port = "80";
            if (request_url.startsWith("https")) {
                port = "443";
            }
            String other = "";
            String sign = sign(mergeSign(time, randomStr, method, uri, host, port, other), mac_key);
            return "MAC " + getAuthorizationParam("id", key_id) + "," + getAuthorizationParam("ts", time) + "," + getAuthorizationParam("nonce", randomStr) + "," + getAuthorizationParam("mac", sign);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getRandomString() {
        String str = Integer.toString(random.nextInt(0x40000000), Character.MAX_RADIX);
        if (str.length() > 5) {
            return str.substring(0, 5);
        } else {
            return str;
        }
    }

    private static String mergeSign(String time, String randomCode, String httpType, String uri, String domain, String port, String other) {
        if (time.isEmpty() || randomCode.isEmpty() || httpType.isEmpty() || domain.isEmpty() || port.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String prefix = time + "\n" + randomCode + "\n" + httpType + "\n" + uri + "\n" + domain + "\n" + port + "\n";
        if (other.isEmpty()) {
            prefix += "\n";
        } else {
            prefix += (other + "\n");
        }
        return prefix;
    }

    private String sign(String signatureBaseString, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] text = signatureBaseString.getBytes(StandardCharsets.UTF_8);
            byte[] signatureBytes = mac.doFinal(text);
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getAuthorizationParam(String key, String value) {
        if (key.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return key + "=" + "\"" + value + "\"";
    }
}
