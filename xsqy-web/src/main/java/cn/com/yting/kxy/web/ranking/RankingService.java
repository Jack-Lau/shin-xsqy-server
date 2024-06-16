/*
 * Created 2018-10-30 16:20:21
 */
package cn.com.yting.kxy.web.ranking;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiService;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.ranking.resource.GenericRankingAward.Model;
import cn.com.yting.kxy.web.ranking.resource.GenericRankingAwardLoader;
import cn.com.yting.kxy.web.ranking.resource.GenericRankingInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Completable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Azige
 */
@Service
public class RankingService implements InitializingBean, ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(RankingService.class);

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private KuaibiService kuaibiService;
    @Autowired
    private MailService mailService;

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;

    private final Map<Long, Object> lockMap = new HashMap<>();
    private TransactionTemplate transactionTemplate;

    private final Cache<Long, List<SimpleRankingRecord>> rankingCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    @Override
    public void afterPropertiesSet() throws Exception {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public SimpleRanking viewRanking(long accountId, long rankingId, int topRecordSize) {
        List<SimpleRankingRecord> allRecords = getRecordsCached(rankingId);

        List<SimpleRankingRecord> selfRecords = allRecords.stream()
            .filter(it -> it.getAccountId() == accountId)
            .collect(Collectors.toList());

        List<SimpleRankingRecord> topRecords = allRecords;
        if (topRecords.size() > topRecordSize) {
            topRecords = topRecords.subList(0, topRecordSize);
        }

        return new SimpleRanking(topRecords, selfRecords);
    }

    public List<SimpleRankingRecord> viewRankingRecords(long accountId, long rankingId) {
        List<SimpleRankingRecord> allRecords = getRecordsCached(rankingId);

        return allRecords.stream()
            .filter(it -> it.getAccountId() == accountId)
            .collect(Collectors.toList());
    }

    private List<SimpleRankingRecord> getRecordsCached(long rankingId) {
        try {
            return rankingCache.get(rankingId, () -> {
                List<RankingRecord> rankingRecords = rankingRepository.findByRankingId(rankingId);
                List<SimpleRankingRecord> simpleRecords = new ArrayList<>();
                for (int i = 0; i < rankingRecords.size(); i++) {
                    RankingRecord rankingRecord = rankingRecords.get(i);
                    simpleRecords.add(new SimpleRankingRecord(rankingRecord.getAccountId(), rankingRecord.getObjectId(), i + 1, Math.abs(rankingRecord.getRankingValue_1())));
                }
                return simpleRecords;
            });
        } catch (ExecutionException ex) {
            throw KxyWebException.internalError("处理排行榜时出现异常", ex);
        }
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, long rankingValue_1) {
        updateRankingValue(rankingId, accountId, objectId, rankingValue_1, 0, 0, 0, 0);
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, long rankingValue_1, long rankingValue_2) {
        updateRankingValue(rankingId, accountId, objectId, rankingValue_1, rankingValue_2, 0, 0, 0);
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, long rankingValue_1, long rankingValue_2, long rankingValue_3) {
        updateRankingValue(rankingId, accountId, objectId, rankingValue_1, rankingValue_2, rankingValue_3, 0, 0);
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, long rankingValue_1, long rankingValue_2, long rankingValue_3, long rankingValue_4) {
        updateRankingValue(rankingId, accountId, objectId, rankingValue_1, rankingValue_2, rankingValue_3, rankingValue_4, 0);
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, long rankingValue_1, long rankingValue_2, long rankingValue_3, long rankingValue_4, long rankingValue_5) {
        updateRankingValue(rankingId, accountId, objectId, new RankingValues(rankingValue_1, rankingValue_2, rankingValue_3, rankingValue_4, rankingValue_5));
    }

    public void updateRankingValue(long rankingId, long accountId, long objectId, RankingValues rankingValues) {
        try {
            synchronized (getLock(rankingId)) {
                transactionTemplate.execute(status -> {
                    RankingRecord record = rankingRepository.findOrCreateById(rankingId, accountId, objectId);
                    record.tryUpdateRankingValues(rankingValues, new Date(timeProvider.currentTime()));
                    return null;
                });
            }
        } catch (TransactionException ex) {
            LOG.error("更新排行榜中发生异常", ex);
        }
    }

    /**
     * 更新指定排行榜的所有数据。对于已存在的记录，如果没有给出对应的值， 则会将其排行值设为 {@link RankingValues#ZERO}。
     * 此方法视排行记录中 accountId 与 objectId 相等
     *
     * @param rankingId
     * @param accountIdToRankingValuesMap
     */
    public void updateAllRankingValueByAccountId(long rankingId, Map<Long, RankingValues> accountIdToRankingValuesMap) {
        Map<Long, Map<Long, RankingValues>> accountIdToObjectIdToRankingValuesMap = accountIdToRankingValuesMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> Collections.singletonMap(entry.getKey(), entry.getValue())));
        updateAllRankingValue(rankingId, accountIdToObjectIdToRankingValuesMap);
    }

    /**
     * 更新指定排行榜的所有数据。对于已存在的记录，如果没有给出对应的值， 则会将其排行值设为 {@link RankingValues#ZERO}
     *
     * @param rankingId
     * @param accountIdToObjectIdToRankingValuesMap
     */
    public void updateAllRankingValue(long rankingId, Map<Long, Map<Long, RankingValues>> accountIdToObjectIdToRankingValuesMap) {
//        LOG.info("开始更新排行榜，id={}", rankingId);
        Map<Long, Map<Long, RankingValues>> map = new HashMap<>();
        accountIdToObjectIdToRankingValuesMap.forEach((k, v) -> {
            map.put(k, new HashMap<>(v));
        });
        Date currentTime = new Date(timeProvider.currentTime());
        try {
            synchronized (getLock(rankingId)) {
                transactionTemplate.execute(status -> {
                    List<RankingRecord> rankingRecords = rankingRepository.findByRankingId(rankingId);
                    rankingRecords.forEach(record -> {
                        Map<Long, RankingValues> objectIdToRankingValuesMap = map.getOrDefault(record.getAccountId(), Collections.emptyMap());
                        RankingValues rankingValues = objectIdToRankingValuesMap.getOrDefault(record.getObjectId(), RankingValues.ZERO);
                        record.tryUpdateRankingValues(rankingValues, currentTime);
                        // 移除已经处理过的条目
                        if (!objectIdToRankingValuesMap.isEmpty()) {
                            objectIdToRankingValuesMap.remove(record.getObjectId());
                        }
                    });

                    // 对剩余的数据库尚未存在记录的条目进行处理
                    map.forEach((accountId, objectIdToRankingValuesMap) -> {
                        objectIdToRankingValuesMap.forEach((objectId, rankingValues) -> {
                            RankingRecord record = rankingRepository.findOrCreateById(rankingId, accountId, objectId);
                            record.tryUpdateRankingValues(rankingValues, currentTime);
                        });
                    });
                    return null;
                });
            }
        } catch (TransactionException ex) {
            LOG.error("更新排行榜中发生异常", ex);
        }
    }

    public void requestResolveAward(long rankingId) {
        resolveAward(resourceContext.getLoader(GenericRankingInfo.class).get(rankingId));
    }

    private void resolveAward(GenericRankingInfo genericRankingInfo) {
        long rebateMilliKuaibiFromOther = Optional.ofNullable(kuaibiService.getLastDayKuaibiDailyRecord())
                .map(it -> it.getRebateMilliKuaibiFromOther())
                .orElse(0L);
        List<Model> models = resourceContext.getByLoaderType(GenericRankingAwardLoader.class).getByAwardModelId(genericRankingInfo.getAwardModel());
        Date currentTime = new Date(timeProvider.currentTime());
        synchronized (getLock(genericRankingInfo.getId())) {
            transactionTemplate.execute(status -> {
                List<RankingRecord> rankingRecords = rankingRepository.findByRankingId(genericRankingInfo.getId());
                int limit = models.size();
                if (rankingRecords.size() < limit) {
                    limit = rankingRecords.size();
                }
                for (int i = 0; i < limit; i++) {
                    RankingRecord record = rankingRecords.get(i);
                    Model model = models.get(i);
                    int purpose = CurrencyConstants.PURPOSE_INCREMENT_战力排行榜奖励;
                    if (genericRankingInfo.getId() == 4430009) {
                        purpose = CurrencyConstants.PURPOSE_INCREMENT_名剑大会1V1排行榜;
                    }
                    MailSendingRequest request = MailSendingRequest.create()
                            .to(record.getAccountId())
                            .template(genericRankingInfo.getMail(), ImmutableMap.of("position", String.valueOf(i + 1)))
                            .attachmentSource(purpose);
                    List<CurrencyStack> currencyStacks = Collections.emptyList();
                    if (model.getWay() == 1) {
                        currencyStacks = model.toCurrencyStacks(rebateMilliKuaibiFromOther);
                    } else if (model.getWay() == 2) {
                        currencyStacks = model.toCurrencyStacks();
                    }
                    currencyStacks.removeIf(it -> it.getCurrencyId() == CurrencyConstants.ID_毫仙石 && it.getAmount() < 1000);
                    if (currencyStacks.isEmpty()) {
                        request.template(RankingConstants.MAIL_ID_NO_AWARD, ImmutableMap.of("position", String.valueOf(i + 1)));
                    } else {
                        request.attachment(currencyStacks);
                    }
                    mailService.sendByRequest(request);
                    if (genericRankingInfo.getReset() == 1) {
                        record.tryUpdateRankingValues(RankingValues.ZERO, currentTime);
                    }
                }
                return null;
            });
        }
    }

    @Override
    public void anyReset(ResetType resetType) {
        resetType.filterStream(resourceContext.getLoader(GenericRankingInfo.class).getAll().values())
            .filter(it -> !RankingConstants.AUTO_RESOLVE_DISABLED_IDS.contains(it.getId()))
                .forEach(genericRankingInfo -> {
                    Throwable throwable = Completable.fromRunnable(() -> resolveAward(genericRankingInfo))
                            // ResettingManager 的重试会导致所有排行榜结算全部重新执行，所以单独处理每个排行榜结算的重试
                            .retry((times, ex) -> {
                                if (times < 5) {
                                    LOG.error("结算排行榜 {} 的奖励时发生异常，第 {} 次，将要重试", genericRankingInfo.getId(), times, ex);
                                    return true;
                                } else {
                                    return false;
                                }
                            })
                            .blockingGet();
                    if (throwable != null) {
                        LOG.error("结算排行榜 {} 的奖励失败", genericRankingInfo.getId(), throwable);
                    }
                });
    }

    private Object getLock(long rankingId) {
        synchronized (lockMap) {
            Object lock = lockMap.get(rankingId);
            if (lock == null) {
                lock = new Object();
                lockMap.put(rankingId, lock);
            }
            return lock;
        }
    }
}
