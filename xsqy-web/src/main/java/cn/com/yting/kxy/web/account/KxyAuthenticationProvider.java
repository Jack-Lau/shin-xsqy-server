/*
 * Created 2018-10-15 12:01:56
 */
package cn.com.yting.kxy.web.account;

import cn.com.yting.kxy.web.account.whitelist.WhiteListStatusHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class KxyAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    @Value("${kxy.web.debug}")
    private boolean debugEnabled;
    @Autowired
    private WhiteListStatusHolder whiteListStatusHolder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AccountStatusUserDetailsChecker accountStatusUserDetailsChecker = new AccountStatusUserDetailsChecker();

    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);;
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Account account;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            authentication = daoAuthenticationProvider.authenticate(authentication);
            account = (Account) authentication.getPrincipal();
        } else if (authentication instanceof KxyThirdPartyAuthenticationToken) {
            KxyThirdPartyAuthenticationToken token = (KxyThirdPartyAuthenticationToken) authentication;
            account = token.getPrincipal();
            accountStatusUserDetailsChecker.check(account);
            token.setAuthenticated(true);
        } else {
            throw new AssertionError("Impossible");
        }

        if (!debugEnabled && whiteListStatusHolder.isEnabled() && !account.isWhiteListed()) {
            throw new DisabledException("暂时禁止登录");
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)
            || KxyThirdPartyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
