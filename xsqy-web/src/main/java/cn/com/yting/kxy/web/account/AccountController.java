/*
 * Created 2018-6-25 16:03:05
 */
package cn.com.yting.kxy.web.account;

import javax.servlet.http.HttpServletRequest;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.web.account.weixin.WeixinLoginResult;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/account")
public class AccountController implements ModuleApiProvider {

    @Value("${kxy.web.debug}")
    private boolean debugEnabled;

    @Autowired
    private AccountService accountService;

    @PostMapping("/register/createTest")
    @DebugOnly
    public Object registerForTest(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
        return accountService.registerForTest(username, password).toAccountInfo();
    }

    @PostMapping("/register/createByPhone")
    public Object registerByPhoneActivation(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("activationCode") int activationCode
    ) {
        return accountService.registerByPhoneActivation(username, password, activationCode).toAccountInfo();
    }

    @PostMapping("/register/requestPhoneActivation")
    public Object requestPhoneActivation(
        @RequestParam("phoneNumber") String phoneNumber
    ) {
        PhoneActivation phoneActivation = accountService.createPhoneActivation(phoneNumber);
        if (debugEnabled) {
            return WebMessageWrapper.ok("String", String.valueOf(phoneActivation.getActivationCode()));
        } else {
            return WebMessageWrapper.ok();
        }
    }

    @RequestMapping("/register/verifyPhoneActivation")
    public Object verifyPhoneActivation(
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam("activationCode") int activationCode
    ) {
        accountService.verifyAndGetPhoneActivation(phoneNumber, activationCode);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/register/resetPassword")
    public Object resetPassword(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("activationCode") int activationCode
    ) {
        return accountService.resetPassword(username, password, activationCode).toAccountInfo();
    }

    @RequestMapping("/view/myself")
    public Object viewMyself(@AuthenticationPrincipal Account account) {
        return account.toAccountInfo();
    }

    @RequestMapping("/login-weixin-success")
    public Object loginWeixinSuccess(HttpServletRequest request) {
        return request.getAttribute("result");
    }

    @PostMapping("/addPassword")
    public AccountInfo addPassword(
        @AuthenticationPrincipal Account account,
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam("activationCode") int activationCode
    ) {
        return accountService.addPassword(account.getId(), username, password, activationCode).toAccountInfo();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .baseUri("/account")
            .name("account")
            // 子模块 register
            .submodule()
            .baseUri("/register")
            .name("register")
            //
            .webInterface()
            .shortName("register")
            .uri("/createTest")
            .description("注册一个测试用账号")
            .post()
            .requestParameter("string", "username", "用户名")
            .requestParameter("string", "password", "密码")
            .response(AccountInfo.class, "成功注册的账号信息")
            .expectableError(AccountException.EC_USERNAME_EXISTED, "用户名已存在")
            .and()
            //
            .webInterface()
            .uri("/createByPhone")
            .description("用手机验证码注册账号")
            .post()
            .requestParameter("string", "username", "用户名，在此处需要是手机号")
            .requestParameter("string", "password", "密码")
            .requestParameter("string", "activationCode", "手机验证码")
            .response(AccountInfo.class, "成功注册的账号信息")
            .expectableError(AccountException.EC_USERNAME_EXISTED, "用户名已存在")
            .expectableError(AccountException.EC_ACODE_NOT_EXIST, "验证码不存在")
            .expectableError(AccountException.EC_ACODE_EXPIRED, "验证码已过期")
            .expectableError(AccountException.EC_ACODE_NOT_VALID, "验证码与手机号不匹配")
            .and()
            //
            .webInterface()
            .uri("/requestPhoneActivation")
            .description("生成一个手机验证码")
            .post()
            .requestParameter("string", "phoneNumber", "用于获取验证码的手机号")
            .requestCaptchaParameters()
            .expectableError(AccountException.EC_REQ_ACODE_TOO_FAST, "申请验证码太快")
            .and()
            //
            .webInterface()
            .uri("/verifyPhoneActivation")
            .description("校验手机验证码是否有效")
            .requestParameter("string", "phoneNumber", "用于获取验证码的手机号")
            .requestParameter("string", "activationCode", "手机验证码")
            .expectableError(AccountException.EC_ACODE_NOT_EXIST, "验证码不存在")
            .expectableError(AccountException.EC_ACODE_EXPIRED, "验证码已过期")
            .expectableError(AccountException.EC_ACODE_NOT_VALID, "验证码与手机号不匹配")
            .and()
            //
            .webInterface()
            .uri("/resetPassword")
            .description("通过手机验证码重设密码")
            .post()
            .requestParameter("string", "username", "用户名，在此处需要是手机号")
            .requestParameter("string", "password", "密码")
            .requestParameter("string", "activationCode", "手机验证码")
            .response(AccountInfo.class, "成功修改密码的账号信息")
            .expectableError(AccountException.EC_ACODE_NOT_EXIST, "验证码不存在")
            .expectableError(AccountException.EC_ACODE_EXPIRED, "验证码已过期")
            .expectableError(AccountException.EC_ACODE_NOT_VALID, "验证码与手机号不匹配")
            .expectableError(AccountException.EC_USERNAME_NOT_EXISTED, "用户名不存在")
            .expectableError(AccountException.EC_PASSCODE_NOT_MODIFIABLE, "指定的账号不可修改密码")
            .and()
            //
            .webInterface()
            .uri("/createTronToken")
            .description("创建一个用于以 Tron 签名登录的随机令牌")
            .post()
            .response("string", "32字节的 hex 编码的令牌")
            .and()
            // 结束 register
            .and()
            //
            .webInterface()
            .shortName("whoami")
            .name("viewMyself")
            .uri("/view/myself")
            .description("查看自己的账号信息")
            .response(AccountInfo.class, "自己的账号信息")
            .and()
            //
            .webInterface()
            .shortName("login")
            .uri("/login")
            .description("用账号名和密码登录一个账号")
            .post()
            .requestParameter("string", "username", "用户名")
            .requestParameter("string", "password", "密码")
            .and()
            //
            .webInterface()
            .shortName("login_weixin")
            .uri("/login-weixin")
            .description("通过微信认证来登录，如果没有账号会自动注册")
            .post()
            .requestParameter("string", "code", "微信认证凭据")
            .requestParameter("string", "from", "表示来源，从普通浏览器为 `web`，从微信浏览器为 `weixin`")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .shortName("login_taptap")
            .uri("/login-taptap")
            .description("通过 Taptap 认证来登录，如果没有账号会自动注册")
            .post()
            .requestParameter("string", "accessToken", "SDK 获取的 access_token")
            .requestParameter("string", "macKey", "SDK 获取的 mac_key")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .shortName("login_apple")
            .uri("/login-apple")
            .description("通过 Apple ID 认证来登录，如果没有账号会自动注册")
            .post()
            .requestParameter("string", "code", "从 Authorization Services 获取的 authorization code")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .shortName("login_ethereum")
            .uri("/login-ethereum")
            .post()
            .description("使用 Ethereum 签名登录，如果没有账号会自动注册")
            .requestParameter("number", "time", "签名的时间戳")
            .requestParameter("string", "address", "Ethereum 账号地址")
            .requestParameter("string", "signature", "签名消息")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .uri("/logout")
            .description("登出当前已登录的账号")
            .post()
            .and()
            //
            .webInterface()
            .uri("/addPassword")
            .post()
            .description("为当前账号添加账号名和密码的登录方式")
            .post()
            .requestParameter("string", "username", "用户名，在此处需要是手机号")
            .requestParameter("string", "password", "密码")
            .requestParameter("string", "activationCode", "手机验证码")
            .response(AccountInfo.class, "成功修改密码的账号信息")
            .expectableError(AccountException.EC_ACODE_NOT_EXIST, "验证码不存在")
            .expectableError(AccountException.EC_ACODE_EXPIRED, "验证码已过期")
            .expectableError(AccountException.EC_ACODE_NOT_VALID, "验证码与手机号不匹配")
            .expectableError(AccountException.EC_USERNAME_EXISTED, "用户名已存在")
            .and()
            //
            .webInterface()
            .uri("/login-kexingqiu")
            .post()
            .description("使用氪星球的身份登录")
            .requestParameter("string", "ticketId", "用 `createTicket` 创建的 ticket id")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .uri("/login-tron")
            .post()
            .description("使用 Tron 签名登录")
            .requestParameter("string", "address", "Tron 地址")
            .requestParameter("string", "token", "用于签名的令牌")
            .requestParameter("string", "sign", "签名")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            //
            .webInterface()
            .uri("/login-qubi")
            .post()
            .description("使用趣币身份登录")
            .requestParameter("string", "openid", "openid")
            .requestParameter("string", "openkey", "openkey")
            .requestParameter("string", "nackname", "nackname（可选）")
            .response(WeixinLoginResult.class, "一些有用的信息")
            .and()
            ;
    }
}
