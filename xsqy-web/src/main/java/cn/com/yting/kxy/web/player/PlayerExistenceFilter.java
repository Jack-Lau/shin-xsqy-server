/*
 * Created 2018-10-10 12:28:40
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.KxyWebUtils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.account.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Azige
 */
public class PlayerExistenceFilter extends OncePerRequestFilter {

    private final PlayerRepository playerRepository;

    public PlayerExistenceFilter(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean continueFilterChain = true;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (request.getMethod().equals("POST") && needToCheck(path)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal != null && principal instanceof Account) {
                    Account account = (Account) principal;
                    if (!playerRepository.existsById(account.getId())) {
                        KxyWebUtils.writeToBannedResponse(response);
                        continueFilterChain = false;
                    }
                }
            }
        }
        if (continueFilterChain) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean needToCheck(String path) {
        if (path.equals("/account/logout") || path.equals("/account/register/resetPassword")) {
            return false;
        }
        if (path.startsWith("/account/login") || path.startsWith("/player/create")) {
            return false;
        }
        return true;
    }
}
