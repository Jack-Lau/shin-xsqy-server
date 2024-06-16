/*
 * Created 2018-7-14 17:36:34
 */
package cn.com.yting.kxy.web.account.weixin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountService;
import cn.com.yting.kxy.web.account.KxyThirdPartyAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.NestedServletException;

/**
 *
 * @author Azige
 */
public class WeixinLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private WeixinApi weixinApiAsWeb;
    @Autowired
    private WeixinApi weixinApiAsPublic;
    @Autowired
    private AccountService accountService;

    public WeixinLoginFilter() {
        super(new AntPathRequestMatcher("/account/login-weixin", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            String code = ServletRequestUtils.getRequiredStringParameter(request, "code");
            String from = ServletRequestUtils.getRequiredStringParameter(request, "from");
            AtomicBoolean newAccount = new AtomicBoolean(false);
            UserInfoResponse userInfo = resolveUserInfo(from, code);
            Account account = accountService.findOrCreateByWeixinUnionId(userInfo.getUnionid(), userInfo.getNickname(), newAccount::set);
            request.setAttribute("accountId", account.getId());
            request.setAttribute("result", new WeixinLoginResult(newAccount.get()));
            return getAuthenticationManager().authenticate(new KxyThirdPartyAuthenticationToken(account));
        } catch (ServletException ex) {
            throw new NestedServletException(ex.getMessage(), ex);
        }
    }

    protected UserInfoResponse resolveUserInfo(String from, String code) throws IOException {
        WeixinApi weixinApi;
        switch (from) {
            case "web":
                weixinApi = weixinApiAsWeb;
                break;
            case "weixin":
                weixinApi = weixinApiAsPublic;
                break;
            default:
                throw new IllegalArgumentException("无效的 from：" + from);
        }
        AccessTokenResponse accessToken = weixinApi.getAccessToken(code);
        if (accessToken.getUnionid() == null) {
            throw new BadCredentialsException("无法通过 code 获取用户信息");
        }
        return weixinApi.getUserInfo(accessToken.getAccess_token(), accessToken.getOpenid());
    }
}
