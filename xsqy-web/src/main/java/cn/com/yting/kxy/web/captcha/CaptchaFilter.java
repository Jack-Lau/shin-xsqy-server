/*
 * Created 2018-9-5 16:46:20
 */
package cn.com.yting.kxy.web.captcha;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Azige
 */
public class CaptchaFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CaptchaFilter.class);

    @Value("${kxy.web.debug}")
    private boolean debugEnabled;

    @Autowired
    private TencentCaptchaApi captchaApi;

    public CaptchaFilter(boolean debugEnabled, TencentCaptchaApi captchaApi) {
        this.debugEnabled = debugEnabled;
        this.captchaApi = captchaApi;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean continueFilterChain = true;

        if (request.getMethod().equals("GET") || request.getMethod().equals("POST")) {
            String ticket = ServletRequestUtils.getStringParameter(request, "ticket");
            String randStr = ServletRequestUtils.getStringParameter(request, "randStr");
            LOG.debug("过滤请求 {}，ticket={}，randStr={}", request.getRequestURI(), ticket, randStr);

            if (ticket == null || ticket.isEmpty() || randStr == null || randStr.isEmpty()) {
                continueFilterChain = false;
            } else if (!captchaApi.verify(ticket, randStr, request.getRemoteAddr())) {
                continueFilterChain = false;
            }
        }

        if (continueFilterChain) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getOutputStream().close();
        }
    }
}
