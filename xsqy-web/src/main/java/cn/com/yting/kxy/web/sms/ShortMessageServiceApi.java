/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.sms;

import java.util.Map;
import java.util.Objects;

import cn.com.yting.kxy.web.KxyWebException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author yush
 */
@Component
public class ShortMessageServiceApi implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(ShortMessageServiceApi.class);

    private static final String product = "Dysmsapi"; //短信API产品名称（短信产品名固定，无需修改）
    private static final String domain = "dysmsapi.aliyuncs.com"; //短信API产品域名（接口地址固定，无需修改）

    @Value("${kxy.web.aliyun.dysmsapi.accessKeyId}")
    private String accessKeyId; //你的accessKeyId,参考本文档步骤2
    @Value("${kxy.web.aliyun.dysmsapi.accessKeySecret}")
    private String accessKeySecret; //你的accessKeySecret，参考本文档步骤2

    @Value("${kxy.web.debug}")
    private boolean debug;

//    final String phoneNumbers = "18612036503,15976633491,13718483267"; //待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
    private final String signName = "块西游"; //短信签名-可在短信控制台中找到
    private final ObjectMapper objectMapper = new ObjectMapper();

    private IClientProfile profile;
    private IAcsClient acsClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        acsClient = new DefaultAcsClient(profile);
    }

    public void sendActivationCode(String phoneNumber, int activationCode) {
        sendShortMessage(
            phoneNumber,
            signName,
            "SMS_140045114",
            ImmutableMap.of("code", String.valueOf(activationCode))
        );
    }

    public void sendKuaibiWithdrawMessage(String phoneNumber, long amount) {
        sendShortMessage(
            phoneNumber,
            signName,
            "SMS_152510569",
            ImmutableMap.of("product", String.valueOf(amount))
        );
    }

    public void sendShortMessage(String phoneNumber, String signName, String templateCode, Map<String, ?> paramMap) {
        if (debug) {
            LOG.info("假装发送了短信：{}", paramMap);
        } else {
            try {
                SendSmsRequest request = new SendSmsRequest();
                request.setMethod(MethodType.POST);
                request.setPhoneNumbers(phoneNumber);
                request.setSignName(signName);
                request.setTemplateCode(templateCode);
                request.setTemplateParam(objectMapper.writeValueAsString(paramMap)); //模板中的变量替换JSON串 如模板内容为"亲爱的${name},您的验证码为${code}"

                LOG.info("将发送短信：{}，至 {}", request.getTemplateParam(), phoneNumber);

                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                if (Objects.equals(sendSmsResponse.getCode(), "OK")) {
                } else {
                    LOG.error("发送短信失败 code:{}", sendSmsResponse.getCode());
                    throw KxyWebException.unknown("无法发送短信");
                }
            } catch (Exception ex) {
                LOG.error("发送短信失败：{}", ex.getMessage());
                throw KxyWebException.unknown("无法发送短信");
            }
        }
    }
}
