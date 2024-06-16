/*
 * Created 2018-9-5 19:20:17
 */
package cn.com.yting.kxy.web.proof;

import java.io.IOException;
import java.time.Duration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.KxyWebUtils;
import cn.com.yting.kxy.web.account.Account;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Azige
 */
public class ClientProoferFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientProoferFilter.class);
    public static String HEADER_NAME_RANDOM_VALUE = "X-Timestamp";
    public static String HEADER_NAME_PROOF_VALUE = "X-Nonce";
    private static final int FAIL_LIMIT = 5;

    private final ClientProofer clientProofer = new ClientProofer();
    private final Cache<Long, AccessRecord> lastVerifyTimeCache = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .build();

    private static class AccessRecord {

        long lastVerifyTime = 0;
        int failCount = 0;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean continueFilterChain = true;
        if (request.getMethod().equals("GET") || request.getMethod().equals("POST")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof Account) {
                Account account = (Account) authentication.getPrincipal();

                String randomValue = request.getHeader(HEADER_NAME_RANDOM_VALUE);
                String proofValue = request.getHeader(HEADER_NAME_PROOF_VALUE);
                boolean proofed = false;
                try {
                    long timestamp = Long.parseLong(randomValue);
                    AccessRecord accessRecord = lastVerifyTimeCache.get(account.getId(), AccessRecord::new);
                    synchronized (accessRecord) {
                        // 基础验证策略是每次的时间戳必须比上次验证的时间戳要大
                        if (timestamp > accessRecord.lastVerifyTime) {
                            proofed = clientProofer.proof(randomValue, proofValue);
                            // 时间戳比之前验证时要大且验证成功时，设置新的验证时间并清空失败次数
                            if (proofed) {
                                accessRecord.lastVerifyTime = timestamp;
                                accessRecord.failCount = 0;
                            }
                        } else {
                            // 如果给出的时间戳不大于之前验证成功的，则在一定次数内仍然验证其是否有效
                            if (accessRecord.failCount < FAIL_LIMIT) {
                                accessRecord.failCount++;
                                proofed = clientProofer.proof(randomValue, proofValue);
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Do nothing
                }

                if (!proofed) {
                    continueFilterChain = false;
                    KxyWebUtils.writeToBannedResponse(response);

                    LOG.info("客户端有效性验证失败，accountId={}", account.getId());
                }
            }
        }

        if (continueFilterChain) {
            filterChain.doFilter(request, response);
        }
    }

}
