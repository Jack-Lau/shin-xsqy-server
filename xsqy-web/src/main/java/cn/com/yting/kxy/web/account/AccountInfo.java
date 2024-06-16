/*
 * Created 2018-7-2 16:43:05
 */
package cn.com.yting.kxy.web.account;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@WebMessageType
@Value
public class AccountInfo {

    private long id;
    private String username;
    private String displayName;
    private List<AccountPasscodeType> passcodeTypes;
}
