/*
 * Created 2018-7-17 18:31:37
 */
package cn.com.yting.kxy.web.account.weixin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponse {

    String openid;
    String nickname;
    int sex;
    String province;
    String city;
    String country;
    String headimgurl;
    List<String> privilege;
    String unionid;
}
