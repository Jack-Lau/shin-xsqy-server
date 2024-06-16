// Created 2021/8/24 16:37

package cn.com.yting.kxy.web.taptap;

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
public class TaptapLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private TapTapApi tapTapApi;
    @Autowired
    private AccountService accountService;

    public TaptapLoginFilter() {
        super(new AntPathRequestMatcher("/account/login-taptap", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            String accessToken = ServletRequestUtils.getRequiredStringParameter(request, "accessToken");
            String macKey = ServletRequestUtils.getRequiredStringParameter(request, "macKey");
            AtomicBoolean newAccount = new AtomicBoolean(false);
            UserInfo userInfo = tapTapApi.getUserInfo(accessToken, macKey);
            if (userInfo == null) {
                throw new BadCredentialsException("Taptap 验证失败");
            }
            Account account = accountService.findOrCreateByTaptapUserId(userInfo.getUnionid(), userInfo.getName(), newAccount::set);
            request.setAttribute("accountId", account.getId());
            request.setAttribute("result", new WeixinLoginResult(newAccount.get()));
            return getAuthenticationManager().authenticate(new KxyThirdPartyAuthenticationToken(account));
        } catch (ServletException ex) {
            throw new NestedServletException(ex.getMessage(), ex);
        }
    }
}
