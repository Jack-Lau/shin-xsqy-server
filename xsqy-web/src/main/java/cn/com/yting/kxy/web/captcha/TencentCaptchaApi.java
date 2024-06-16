/*
 * Created 2018-9-5 15:40:47
 */
package cn.com.yting.kxy.web.captcha;

import java.io.IOException;

import cn.com.yting.kxy.web.KxyWebException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 *
 * @author Azige
 */
@Component
public class TencentCaptchaApi implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(TencentCaptchaApi.class);

    private static final String URL = "https://ssl.captcha.qq.com/ticket/verify";
    private static final UriTemplate TEMPLATE = new UriTemplate(
        URL
        + "?aid={aid}"
        + "&AppSecretKey={AppSecretKey}"
        + "&Ticket={Ticket}"
        + "&Randstr={Randstr}"
        + "&UserIP={UserIP}"
    );

    @Value("${kxy.web.captcha.aid}")
    private String aid;
    @Value("${kxy.web.captcha.secretKey}")
    private String secretKey;

    @Autowired
    private ClientHttpRequestFactory requestFactory;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestTemplate restTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new RestTemplate(requestFactory);
    }

    public boolean verify(String ticket, String randStr, String userIp) {
        LOG.debug("进行人机验证，ticket={}，randStr={}，userIp={}", ticket, randStr, userIp);
        try {
            String responseText = restTemplate.getForObject(TEMPLATE.expand(aid, secretKey, ticket, randStr, userIp), String.class);
            CaptchaVerifyResponse response = objectMapper.readValue(responseText, CaptchaVerifyResponse.class);
            if (response.getResponse() == 1) {
                LOG.debug("人机验证成功，ip={}，response={}", userIp, responseText);
                return true;
            } else {
                LOG.debug("人机验证失败，ip={}，response={}", userIp, responseText);
                return false;
            }
        } catch (IOException ex) {
            throw KxyWebException.internalError(ex.getMessage(), ex);
        }
    }
}
