/*
 * Created 2018-9-5 16:16:59
 */
package cn.com.yting.kxy.web.captcha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaVerifyResponse {

    private int response;
    private int evil_level;
    private String err_msg;
}
