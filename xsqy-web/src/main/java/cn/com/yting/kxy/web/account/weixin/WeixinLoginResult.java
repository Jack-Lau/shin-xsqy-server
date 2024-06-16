/*
 * Created 2018-7-25 18:48:33
 */
package cn.com.yting.kxy.web.account.weixin;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class WeixinLoginResult {

    private boolean newAccount;
}
