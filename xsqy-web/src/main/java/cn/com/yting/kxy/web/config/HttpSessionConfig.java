package cn.com.yting.kxy.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Created 2021/1/12 11:09

/**
 *
 * @author Azige
 */
@Configuration
@EnableSpringHttpSession
class HttpSessionConfig {

    private static final int SESSION_TTL = 7 * 24 * 3600_000;

    @Bean
    public SessionRepository sessionRepository() {
        MapSessionRepository bean = new MapSessionRepository(new ConcurrentHashMap<>());
        bean.setDefaultMaxInactiveInterval(SESSION_TTL);
        return bean;
    }

    // 同时在 HTTP 头和 Cookie 里设置和解析 Session ID
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HttpSessionIdResolver() {
            HeaderHttpSessionIdResolver header = HeaderHttpSessionIdResolver.xAuthToken();
            CookieHttpSessionIdResolver cookie = new CookieHttpSessionIdResolver();

            {
                DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
                cookieSerializer.setCookieMaxAge(SESSION_TTL);
                cookie.setCookieSerializer(cookieSerializer);
            }

            @Override
            public List<String> resolveSessionIds(HttpServletRequest request) {
                List<String> list = new ArrayList<>();
                list.addAll(header.resolveSessionIds(request));
                list.addAll(cookie.resolveSessionIds(request));
                list.addAll(resolveFromQueryParameter(request));
                return list;
            }

            private List<String> resolveFromQueryParameter(HttpServletRequest request) {
                String sessionid = request.getParameter("sessionid");
                if (sessionid != null) {
                    return Collections.singletonList(sessionid);
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public void setSessionId(
                HttpServletRequest request,
                HttpServletResponse response,
                String sessionId
            ) {
                header.setSessionId(request, response, sessionId);
                cookie.setSessionId(request, response, sessionId);
            }

            @Override
            public void expireSession(HttpServletRequest request, HttpServletResponse response) {
                header.expireSession(request, response);
                cookie.expireSession(request, response);
            }
        };
    }
}
