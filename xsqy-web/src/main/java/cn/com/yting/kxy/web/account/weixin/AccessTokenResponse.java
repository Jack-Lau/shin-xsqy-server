/*
 * Created 2018-7-14 17:18:25
 */
package cn.com.yting.kxy.web.account.weixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    String access_token;
    long expires_in;
    String refresh_token;
    String openid;
    String scope;
    String unionid;
}
