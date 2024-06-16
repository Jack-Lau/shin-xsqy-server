/*
 * Created 2019-2-19 17:29:44
 */
package cn.com.yting.kxy.web.topone;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.signing.TopOneSigner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Azige
 */
public class TopOneApi implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(TopOneApi.class);
    private static final String ROOT_URL = "https://open.top1.one";

    @Value("${kxy.web.topOne.appId}")
    private String appId;
    @Value("${kxy.web.topOne.key}")
    private String key;
    @Value("${kxy.web.rootUrl}")
    private String kxyRootUrl;

    @Autowired
    private ClientHttpRequestFactory requestFactory;
    @Autowired
    private TimeProvider timeProvider;

    private TopOneSigner signer;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new RestTemplate(requestFactory);
        signer = new TopOneSigner(key);
    }

    public AccessTokenResponse accessToken(String code) {
        Random random = RandomProvider.getRandom();
        int nonce = Math.abs(random.nextInt());
        long currentTime = timeProvider.currentTime() / 1000;
        String sign = signer.start()
            .parameter("appid", appId)
            .parameter("code", code)
            .parameter("grant_type", "authorization_code")
            .parameter("timestamp", currentTime)
            .parameter("nonce", nonce)
            .sign();
        URI uri = UriComponentsBuilder.fromHttpUrl(ROOT_URL + "/api/oauth2/access_token")
            .queryParam("appid", appId)
            .queryParam("code", code)
            .queryParam("grant_type", "authorization_code")
            .queryParam("timestamp", currentTime)
            .queryParam("nonce", nonce)
            .queryParam("sign", sign)
            .build().toUri();
        return processResponse(restTemplate.getForObject(uri, String.class), AccessTokenResponse.class);
    }

    public UserInfoResponse userInfo(String accessToken, String openId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(ROOT_URL + "/api/oauth2/userinfo")
            .queryParam("access_token", accessToken)
            .queryParam("openid", openId)
            .build().toUri();
        return processResponse(restTemplate.getForObject(uri, String.class), UserInfoResponse.class);
    }

    public UnifiedOrderResponse createOrder(long amount, String orderNumber, String body, String attach, String callbackUrl, String accessToken, String openId) {
        Random random = RandomProvider.getRandom();
        int nonce = Math.abs(random.nextInt());
        long currentTime = timeProvider.currentTime() / 1000;
        String coin = "TODO";
        String lang = "zh-CN";
        String notifyUrl = kxyRootUrl + "/public/topOne/orderNotify";
        String sign = signer.start()
            .parameter("coin", coin)
            .parameter("amount", amount)
            .parameter("out_trade_no", orderNumber)
            .parameter("body", body)
            .parameter("attach", attach)
            .parameter("callback_url", callbackUrl)
            .parameter("notify_url", notifyUrl)
            .parameter("appid", appId)
            .parameter("access_token", accessToken)
            .parameter("openid", openId)
            .parameter("timestamp", currentTime)
            .parameter("nonce", nonce)
            .parameter("lang", lang)
            .sign();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("coin", coin);
        params.add("amount", String.valueOf(amount));
        params.add("out_trade_no", orderNumber);
        params.add("body", body);
        params.add("attach", attach);
        params.add("callback_url", callbackUrl);
        params.add("notify_url", notifyUrl);
        params.add("appid", appId);
        params.add("access_token", accessToken);
        params.add("openid", openId);
        params.add("timestamp", String.valueOf(currentTime));
        params.add("nonce", String.valueOf(nonce));
        params.add("sign", sign);
        params.add("lang", lang);
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);

        return processResponse(restTemplate.postForObject(ROOT_URL + "/api/pay/unifiedorder", entity, String.class), UnifiedOrderResponse.class);
    }

    public OrderQueryResponse orderQuery(String orderNumber) {
        Random random = RandomProvider.getRandom();
        int nonce = Math.abs(random.nextInt());
        long currentTime = timeProvider.currentTime() / 1000;
        String sign = signer.start()
            .parameter("out_trade_no", orderNumber)
            .parameter("appid", appId)
            .parameter("timestamp", currentTime)
            .parameter("nonce", nonce)
            .sign();
        URI uri = UriComponentsBuilder.fromHttpUrl(ROOT_URL + "/api/pay/orderquery")
            .queryParam("out_trade_no", orderNumber)
            .queryParam("appid", appId)
            .queryParam("timestamp", currentTime)
            .queryParam("nonce", nonce)
            .queryParam("sign", sign)
            .build().toUri();
        return processResponse(restTemplate.getForObject(uri, String.class), OrderQueryResponse.class);
    }

    public TransferResponse transfer(String openId, long amount) {
        Random random = RandomProvider.getRandom();
        int nonce = Math.abs(random.nextInt());
        long currentTime = timeProvider.currentTime() / 1000;
        String coin = "TODO";
        String sign = signer.start()
            .parameter("coin", coin)
            .parameter("amount", amount)
            .parameter("appid", appId)
            .parameter("openid", openId)
            .parameter("timestamp", currentTime)
            .parameter("nonce", nonce)
            .sign();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("coin", coin);
        params.add("amount", String.valueOf(amount));
        params.add("appid", appId);
        params.add("openid", openId);
        params.add("timestamp", String.valueOf(currentTime));
        params.add("nonce", String.valueOf(nonce));
        params.add("sign", sign);
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);

        return processResponse(restTemplate.postForObject(ROOT_URL + "/api/pay/transfer", entity, String.class), TransferResponse.class);
    }

    private <T> T processResponse(String responseString, Class<T> type) {
        try {
            @SuppressWarnings("unchecked")
            JsonNode root = objectMapper.readTree(responseString);
            JsonNode code = root.get("code");
            if (code != null && code.asInt() != 0) {
                String msgString = "";
                JsonNode msg = root.get("msg");
                if (msg != null) {
                    msgString = msg.asText();
                }
                throw new TopOneApiException(msgString);
            }
            JsonNode data = root.get("data");
            if (data != null) {
                return objectMapper.convertValue(data, type);
            } else {
                return null;
            }
        } catch (IOException ex) {
            throw new TopOneApiException(ex);
        }
    }
}
