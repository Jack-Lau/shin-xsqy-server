/*
 * Created 2018-11-3 16:06:16
 */
package cn.com.yting.kxy.web.filters;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.com.yting.kxy.web.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Azige
 */
public class RequestRejectedFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestRejectedFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RequestRejectedException ex) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Optional.ofNullable(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
                    .map(it -> ((SecurityContext) it).getAuthentication())
                    .map(it -> it.getPrincipal())
                    .ifPresent(principal -> {
                        if (principal instanceof Account) {
                            Account account = (Account) principal;
                            LOG.info("请求的 URL 非法，accountId={}", account.getId());
                        }
                    });
                session.invalidate();
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
