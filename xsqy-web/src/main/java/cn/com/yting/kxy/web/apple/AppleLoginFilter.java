// Created 2021/8/24 16:37

package cn.com.yting.kxy.web.apple;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountService;
import cn.com.yting.kxy.web.account.KxyThirdPartyAuthenticationToken;
import cn.com.yting.kxy.web.account.weixin.WeixinLoginResult;
import cn.com.yting.kxy.web.taptap.TapTapApi;
import cn.com.yting.kxy.web.taptap.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Azige
 */
public class AppleLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private AppleApi appleApi;
    @Autowired
    private AccountService accountService;

    public AppleLoginFilter() {
        super(new AntPathRequestMatcher("/account/login-apple", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            String code = ServletRequestUtils.getRequiredStringParameter(request, "code");
            AtomicBoolean newAccount = new AtomicBoolean(false);
            TokenResponse tokenResponse = appleApi.generateToken(code);
            String userId = tokenResponse.extractUserId();
            if (userId == null) {
                throw new BadCredentialsException("Apple 验证失败");
            }
            Account account = accountService.findOrCreateByAppleId(userId, newAccount::set);
            request.setAttribute("accountId", account.getId());
            request.setAttribute("result", new WeixinLoginResult(newAccount.get()));
            return getAuthenticationManager().authenticate(new KxyThirdPartyAuthenticationToken(account));
        } catch (ServletException ex) {
            throw new NestedServletException(ex.getMessage(), ex);
        }
    }
}
