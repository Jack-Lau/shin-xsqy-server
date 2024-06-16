/*
 * Created 2019-1-4 11:32:53
 */
package cn.com.yting.kxy.web.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 *
 * @author Azige
 */
public class PathTokenRememberMeServices extends TokenBasedRememberMeServices {

    private static final Pattern TOKEN_PATTERN = Pattern.compile(";token=([0-9a-zA-Z]*)");

    public PathTokenRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request) {
        String token = super.extractRememberMeCookie(request);
        if (token == null) {
            String requestURI = request.getRequestURI();
            Matcher matcher = TOKEN_PATTERN.matcher(requestURI);
            if (matcher.find()) {
                token = matcher.group(1);
            }
        }
        return token;
    }
}
