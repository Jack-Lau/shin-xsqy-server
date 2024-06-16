/*
 * Created 2018-7-4 18:59:17
 */
package cn.com.yting.kxy.web.account;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class AccountException extends KxyWebException {

    public static final int EC_USERNAME_EXISTED = 100;
    public static final int EC_REQ_ACODE_TOO_FAST = 101;
    public static final int EC_ACODE_NOT_EXIST = 102;
    public static final int EC_ACODE_EXPIRED = 103;
    public static final int EC_ACODE_NOT_VALID = 104;
    public static final int EC_USERNAME_NOT_EXISTED = 105;
    public static final int EC_PASSCODE_NOT_MODIFIABLE = 106;
    public static final int EC_ILLEGAL_PHONE_NUMBER = 107;
    public static final int EC_USERNAME_EXISTED2 = 108;
    public static final int EC_ACCOUNT_LOCKED = 109;
    public static final int EC_WHITE_LIST_ENABLED = 110;

    public AccountException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static AccountException usernameExisted(String username) {
        return new AccountException(EC_USERNAME_EXISTED, "用户名已被注册：" + username);
    }

    public static AccountException requestActivationCodeTooFast() {
        return new AccountException(EC_REQ_ACODE_TOO_FAST, "申请验证码的间隔时间未到");
    }

    public static AccountException activationCodeNotExist() {
        return new AccountException(EC_ACODE_NOT_EXIST, "验证码不存在");
    }

    public static AccountException activationCodeExpired() {
        return new AccountException(EC_ACODE_EXPIRED, "验证码已过期");
    }

    public static AccountException activationCodeNotValid() {
        return new AccountException(EC_ACODE_NOT_VALID, "手机号不一致");
    }

    public static AccountException passcodeNotModifiable() {
        return new AccountException(EC_PASSCODE_NOT_MODIFIABLE, "此账号的通行凭证不能修改");
    }

    public static AccountException illegalPhoneNumber() {
        return new AccountException(EC_ILLEGAL_PHONE_NUMBER, "无效的手机号");
    }

    public static AccountException usernameExisted2(String username) {
        return new AccountException(EC_USERNAME_EXISTED2, "用户名已被注册：" + username);
    }

    public static AccountException whiteListEnabled() {
        return new AccountException(EC_WHITE_LIST_ENABLED, "暂时禁止登录");
    }
}
