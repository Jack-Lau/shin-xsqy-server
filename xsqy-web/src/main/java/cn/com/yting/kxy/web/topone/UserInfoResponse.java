/*
 * Created 2019-2-20 15:32:16
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
public class UserInfoResponse {

    private String openid;
    private String nation_code;
    private String phone;
    private String email;
}
