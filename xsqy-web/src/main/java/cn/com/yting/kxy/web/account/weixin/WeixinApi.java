/*
 * Created 2018-7-14 15:38:50
 */
package cn.com.yting.kxy.web.account.weixin;

import java.io.IOException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 *
 * @author Azige
 */
public class WeixinApi implements InitializingBean {

    private static final String URL_API_ROOT = "https://api.weixin.qq.com";

    private static final String URL_ACCESS_TOKEN = URL_API_ROOT + "/sns/oauth2/access_token";
    private static final UriTemplate TEMPLATE_ACCESS_TOKEN = new UriTemplate(
        URL_ACCESS_TOKEN
        + "?appid={appid}"
        + "&secret={secret}"
        + "&code={code}"
        + "&grant_type=authorization_code"
    );

    private static final String URL_USER_INFO = URL_API_ROOT + "/sns/userinfo";
    private static final UriTemplate TEMPLATE_USER_INFO = new UriTemplate(
        URL_USER_INFO
        + "?access_token={access_token}"
        + "&openid={openid}"
    );

    private final String appId;
    private final String secretKey;

    @Autowired
    private ClientHttpRequestFactory requestFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate;

    public WeixinApi(String appId, String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    public AccessTokenResponse getAccessToken(String code) throws IOException {
        String responseJson = restTemplate.getForObject(TEMPLATE_ACCESS_TOKEN.expand(appId, secretKey, code), String.class);
        return objectMapper.readValue(responseJson, AccessTokenResponse.class);
    }

    public UserInfoResponse getUserInfo(String accessToken, String openId) throws IOException {
        String responseJson = restTemplate.getForObject(TEMPLATE_USER_INFO.expand(accessToken, openId), String.class);
        return objectMapper.readValue(responseJson, UserInfoResponse.class);
    }
}
