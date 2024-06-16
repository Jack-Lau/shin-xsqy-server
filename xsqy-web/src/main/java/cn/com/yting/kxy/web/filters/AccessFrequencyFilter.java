/*
 * Created 2018-8-30 12:37:37
 */
package cn.com.yting.kxy.web.filters;

import cn.com.yting.kxy.web.KxyWebUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Azige
 */
public class AccessFrequencyFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AccessFrequencyFilter.class);

    private final Cache<String, AtomicInteger> accessFrequencyMap = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build();
    private final Cache<String, DenyRecord> deniedClients = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.DAYS)
        .build();

    private int limit;

    private static class DenyRecord {
        long timeToEnd;
        int count;
    }

    public AccessFrequencyFilter(int limit) {
        this.limit = limit;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        DenyRecord denyRecord = deniedClients.getIfPresent(request.getRemoteAddr());
        if (denyRecord != null && denyRecord.timeToEnd > System.currentTimeMillis()) {
            KxyWebUtils.writeToBannedResponse(response);
        } else {
            String method = request.getMethod();
            try {
                if (method.equals("POST") || method.equals("GET")) {
                    AtomicInteger frequency = accessFrequencyMap.get(request.getRemoteAddr(), () -> new AtomicInteger(0));
                    frequency.incrementAndGet();
//                    if (method.equals("GET")) {
//                        frequency.incrementAndGet();
//                    } else {
//                        frequency.addAndGet(2);
//                    }
                    if (frequency.get() >= limit) {
                        LOG.info("{} 访问过于频繁，将禁止访问", request.getRemoteAddr());
                        denyRecord = deniedClients.get(request.getRemoteAddr(), DenyRecord::new);
                        denyRecord.timeToEnd = System.currentTimeMillis() + (long) (600_000 * Math.pow(2, denyRecord.count));
                        denyRecord.count++;
                    }
                }
                filterChain.doFilter(request, response);
            } catch (ExecutionException ex) {
                assert false;
            }
        }
    }
}
