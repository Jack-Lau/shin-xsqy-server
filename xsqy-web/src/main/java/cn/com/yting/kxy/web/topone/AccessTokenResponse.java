/*
 * Created 2019-2-20 10:50:29
 */
package cn.com.yting.kxy.web.topone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    private String access_token;
    private long expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
}
