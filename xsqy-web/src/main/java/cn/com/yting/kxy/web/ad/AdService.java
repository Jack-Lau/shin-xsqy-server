// Created 2021/8/25 16:16

package cn.com.yting.kxy.web.ad;

import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

/**
 * @author Azige
 */
@Service
public class AdService {

    private static final Logger LOG = LoggerFactory.getLogger(AdService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AwardService awardService;
    @Autowired
    private AdProperties adProperties;

    private final Cache<String, Object> adRequestCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .build();
    private final Object adRequestCacheLock = new Object();
    private final Object adRequestCacheDummy = new Object();

    public boolean verifyAndPublishEvent(
        String pid,
        long accountId,
        String transactionId,
        String extra,
        String signature
    ) {
        LOG.debug("尝试验证广告回调，pid={}, accountId={}, transactionId={}, extra={}, signature={}", pid, accountId, transactionId, extra, signature);
        try {
            String secret = adProperties.getSecrets().get(pid);
            if (secret == null) {
                LOG.warn("未知的 pid：{}", pid);
                return false;
            }
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            String expectedSignature = Hex.encodeHexString(sha256.digest((transactionId + ":" + secret).getBytes(StandardCharsets.UTF_8)));
            LOG.debug("期望的签名：{}", expectedSignature);
            if (!expectedSignature.equals(signature)) {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        synchronized (adRequestCacheLock) {
            if (adRequestCache.getIfPresent(transactionId) == null) {
                adRequestCache.put(transactionId, adRequestCacheDummy);

                eventPublisher.publishEvent(new AdRewardEvent(accountId, transactionId, extra));

                awardService.processAward(accountId, 5061, CurrencyConstants.PURPOSE_INCREMENT_看广告);
            }
        }

        return true;
    }
}
