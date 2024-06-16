/*
 * Created 2018-10-17 16:43:48
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.battle.Unit;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import cn.com.yting.kxy.battle.event.BattleEvent.BattleEventType;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.battle.BattleService;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.game.minearena.resource.ArenaRankInfo;
import cn.com.yting.kxy.web.game.minearena.resource.ArenaRankInfoLoader;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.azige.mgxy.event.EventHandler;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class MineArenaService implements ResetTask, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(MineArenaService.class);

    @Autowired
    private MineArenaRepository mineArenaRepository;
    @Autowired
    private PitRepository pitRepository;
    @Autowired
    private PitPositionChangeLogRepository pitPositionChangeLogRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private MineArenaRewardObtainLogRepository mineArenaRewardObtainLogRepository;
    @Autowired
    private MineArenaChallengeLogRepository mineArenaChallengeLogRepository;

    @Autowired
    @Lazy
    private MineArenaService self;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private BattleService battleService;
    @Autowired
    private AwardService awardService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private final Object creationLock = new Object();
    /**
     * 挑战者位置 &lt;-&gt; 被挑战者位置 的双向映射
     */
    private final BiMap<Long, Long> challengeRelationMap = HashBiMap.create();
    private final Map<Long, Challenge> challengeMap = new HashMap<>();
    private final AtomicLong nextChallengeId = new AtomicLong(1);
    private TransactionTemplate transactionTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
    }

    public MineArenaComplex createRecord(long accountId) {
        if (mineArenaRepository.existsById(accountId)) {
            throw MineArenaException.alreadyCreated();
        }
        if (playerRepository.findById(accountId).get().getPlayerLevel() < MineArenaConstants.PLAYER_LEVEL_REQUIREMENT) {
            throw MineArenaException.playerLevelNotMeetRequirement();
        }

        synchronized (creationLock) {
            long position = pitRepository.count() + 1;
            Pit pit = new Pit();
            pit.setPosition(position);
            pit.setAccountId(accountId);
            pit = pitRepository.save(pit);

            logPitPositionChange(accountId, MineArenaConstants.POSITION_INIT, position);

            MineArenaRecord record = new MineArenaRecord();
            record.setAccountId(accountId);
            record.setLastRewardResolveTime(new Date(timeProvider.currentTime()));
            record = mineArenaRepository.save(record);
            return new MineArenaComplex(record, wrapPitWithDetail(pit));
        }
    }

    public List<PitDetail> findRandomValidChallengingPits(long accountId) {
        final int sizeLimit = 20;

        Pit challengerPit = pitRepository.findByAccountIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("竞技场记录不存在"));

        List<Pit> pits;
        if (challengerPit.getPosition() <= 3) {
            pits = pitRepository.findOrderByPosition(PageRequest.of(0, 4));
        } else {
            RankingRange range = getValidChallengingPositionRange(challengerPit.getPosition());
            List<Long> candidates = null;
            if (range.getUpperBound() - range.getLowerBound() > sizeLimit) {
                List<Long> list = LongStream.rangeClosed(range.getLowerBound(), range.getUpperBound())
                        .filter(it -> !challengeRelationMap.containsKey(it) && !challengeRelationMap.containsValue(it))
                        .mapToObj(Long::valueOf)
                        .collect(Collectors.toList());
                if (list.size() >= sizeLimit) {
                    candidates = list;
                }
            }
            if (candidates == null) {
                candidates = LongStream.rangeClosed(range.getLowerBound(), range.getUpperBound())
                        .mapToObj(Long::valueOf)
                        .collect(Collectors.toList());
            }
            Collections.shuffle(candidates);
            if (candidates.size() > sizeLimit) {
                candidates = candidates.subList(0, sizeLimit);
            }
            pits = pitRepository.findAllById(candidates);
        }

        pits.removeIf(it -> it.getPosition() == challengerPit.getPosition());

        return wrapPitsWithDetail(pits);
    }

    public StartChallengeResult startChallenge(long accountId, long targetPosition, long ybToUse) {
        MineArenaRecord arenaRecord = mineArenaRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("竞技场记录不存在"));
        Pit challengerPit = pitRepository.findByAccountIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("竞技场记录不存在"));
        if (!getValidChallengingPositionRange(challengerPit.getPosition()).isInRange(targetPosition)) {
            throw KxyWebException.unknown("挑战的目标排名不在有效范围内");
        }
        Pit defenderPit = pitRepository.findById(targetPosition).orElseThrow(() -> KxyWebException.unknown("挑战的排名不存在"));
        int challengePrice = getChallengePrice(defenderPit);
        int challengePointToUse = (int) (challengePrice - ybToUse * MineArenaConstants.RATIO_YB_TO_CP);
        if (challengePointToUse < 0) {
            challengePointToUse = 0;
        }
        if (arenaRecord.getChallengePoint() < challengePointToUse) {
            throw MineArenaException.insufficientChallengePoint();
        }
        synchronized (challengeMap) {
            if (challengeRelationMap.containsKey(challengerPit.getPosition()) || challengeRelationMap.containsValue(challengerPit.getPosition())
                    || challengeRelationMap.containsKey(defenderPit.getPosition()) || challengeRelationMap.containsValue(defenderPit.getPosition())) {
                throw MineArenaException.locked();
            }

            arenaRecord.decreaseChallengePoint(challengePointToUse);
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, ybToUse, true, CurrencyConstants.PURPOSE_DECREMENT_抢占摇钱树挑战费用);
            defenderPit.increaseChallengedCount();

            long challengerPitPosition = challengerPit.getPosition();
            Date deadline = Date.from(timeProvider.currentInstant().plus(MineArenaConstants.DURATION_MAX_CHALLENGE_TIME));
            long challengeId = nextChallengeId.getAndIncrement();
            challengeMap.put(challengeId, new Challenge(challengerPitPosition, deadline, challengePrice));
            challengeRelationMap.put(challengerPit.getPosition(), defenderPit.getPosition());

            BattleSession battleSession = battleService.startAsyncPVP(accountId, Collections.singletonList(defenderPit.getAccountId()), false, false, Collections.singletonList(EventHandler.of(event -> {
                boolean challengeSuccess = event.getBattleDirector().getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED;
                self.concludeChallenge(challengeId, challengeSuccess);
            }, BattleEventType.BATTLE_END, 0)));

            eventPublisher.publishEvent(new MineArenaChallengeStartEvent(this, arenaRecord));
            return new StartChallengeResult(battleSession.getId());
        }
    }

    public void concludeChallenge(long challengeId, boolean challengeSuccess) {
        synchronized (challengeMap) {
            Challenge challenge = challengeMap.get(challengeId);
            if (challenge == null) {
                return;
            }

            long challengerPitPosition = challenge.getChallengerPitPosition();
            long defenderPitPosition = challengeRelationMap.get(challengerPitPosition);
            Pit challengerPit = pitRepository.findByIdForWrite(challengerPitPosition).get();
            Pit defenderPit = pitRepository.findByIdForWrite(defenderPitPosition).get();
            long challengerAccountId = challengerPit.getAccountId();
            long defenderAccountId = defenderPit.getAccountId();

            MineArenaChallengeLog challengeLog = new MineArenaChallengeLog();
            challengeLog.setChallengerAccountId(challengerAccountId);
            challengeLog.setDefenderAccountId(defenderAccountId);
            challengeLog.setCost(challenge.getPrice());
            challengeLog.setChallengerPosition(challengerPitPosition);
            challengeLog.setDefenderPosition(defenderPitPosition);
            challengeLog.setSuccess(challengeSuccess);
            challengeLog.setEventTime(new Date(timeProvider.currentTime()));
            mineArenaChallengeLogRepository.save(challengeLog);

            if (challengeSuccess) {
                challengerPit.setAccountId(defenderAccountId);
                defenderPit.setAccountId(challengerAccountId);
                logPitPositionChange(challengerAccountId, challengerPitPosition, defenderPitPosition);
                logPitPositionChange(defenderAccountId, defenderPitPosition, challengerPitPosition);

                awardService.processAward(challengerAccountId, MineArenaConstants.AWARD_ID_SUCCESS);

            } else {
                awardService.processAward(challengerAccountId, MineArenaConstants.AWARD_ID_FAILURE);
            }

            challengeRelationMap.remove(challengerPitPosition);
            challengeMap.remove(challengeId);

            eventPublisher.publishEvent(new MineArenaChallengeCompletedEvent(this, challengeLog));
        }
    }

    private CurrencyStack calculateReward(long playerFc, ArenaRankInfo arenaRankInfo, LocalTime startTime, LocalTime endTime) {
        double durationHours = Duration.between(startTime, endTime).getSeconds() / 3600d;
        double base = 0.0000625 * Math.pow((double) playerFc / 100.0, 2.0);
        if (arenaRankInfo != null) {
            return new CurrencyStack(arenaRankInfo.getCurrency(), (long) (durationHours * base * arenaRankInfo.getFactor()));
        } else {
            return new CurrencyStack(MineArenaConstants.OUTER_RANKING_CURRENCY_ID, (long) (durationHours * base * MineArenaConstants.OUTER_RANKING_EFFICIENCY));
        }
    }

    private List<CurrencyStack> calculateRewards(List<PitPositionChangeLog> logs, LocalTime endTime) {
        ArenaRankInfoLoader rankInfoLoader = resourceContext.getByLoaderType(ArenaRankInfoLoader.class);
        Map<Long, Long> currencyChance = new HashMap<>();
        LocalTime lastTime = LocalTime.MIDNIGHT;
        PitPositionChangeLog lastLog = null;
        for (PitPositionChangeLog log : logs) {
            LocalTime eventTime = TimeUtils.toOffsetTime(log.getEventTime()).toLocalTime();
            if (log.getBeforePosition() != MineArenaConstants.POSITION_INIT) {
                ArenaRankInfo arenaRankInfo = rankInfoLoader.getByRanking((int) log.getBeforePosition());
                long playerFc = playerRepository.findById(log.getAccountId()).get().getFc();
                CurrencyStack stack = calculateReward(playerFc, arenaRankInfo, lastTime, eventTime);
                currencyChance.merge(stack.getCurrencyId(), stack.getAmount(), Long::sum);
            }
            lastTime = eventTime;
            lastLog = log;
        }
        if (lastLog != null && lastTime.isBefore(endTime)) {
            ArenaRankInfo arenaRankInfo = rankInfoLoader.getByRanking((int) lastLog.getAfterPosition());
            long playerFc = playerRepository.findById(lastLog.getAccountId()).get().getFc();
            CurrencyStack stack = calculateReward(playerFc, arenaRankInfo, lastTime, endTime);
            currencyChance.merge(stack.getCurrencyId(), stack.getAmount(), Long::sum);
        }
        return currencyChance.entrySet().stream()
                .map(it -> new CurrencyStack(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

    private List<CurrencyStack> calculateRewardsInDay(long accountId, LocalDate date, LocalTime until) {
        List<PitPositionChangeLog> logs = pitPositionChangeLogRepository.findByAccountIdAndDate(accountId, date);
        List<CurrencyStack> currencyStacks;
        if (!logs.isEmpty()) {
            currencyStacks = calculateRewards(logs, until);
        } else {
            PitPositionChangeLog log = pitPositionChangeLogRepository.findLatestByAccountIdBeforeDate(accountId, date);
            if (log == null) {
                currencyStacks = Collections.emptyList();
            } else {
                ArenaRankInfo arenaRankInfo = resourceContext.getByLoaderType(ArenaRankInfoLoader.class).getByRanking((int) log.getAfterPosition());
                long playerFc = playerRepository.findById(log.getAccountId()).get().getFc();
                CurrencyStack currencyStack = calculateReward(playerFc, arenaRankInfo, LocalTime.MIN, until);
                currencyStacks = Collections.singletonList(currencyStack);
            }
        }

        return currencyStacks;
    }

    public List<CurrencyStack> getTodayRewardUntilNow(long accountId) {
        return calculateRewardsInDay(accountId, timeProvider.today(), timeProvider.currentOffsetDateTime().toLocalTime());
    }

    public MineArenaRecord resolveReward(long accountId) {
        MineArenaRecord record = mineArenaRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("竞技场记录不存在"));
        if (!TimeUtils.toOffsetTime(record.getLastRewardResolveTime()).toLocalDate().isBefore(timeProvider.today())) {
            return record;
        }

        List<CurrencyStack> currencyStacks = calculateRewardsInDay(accountId, timeProvider.yesterday(), LocalTime.MAX);
        record.setResolvedReward(CurrencyStack.listToText(currencyStacks));
        record.setResolvedRewardDelivered(false);
        record.setLastRewardResolveTime(new Date(timeProvider.currentTime()));

        return record;
    }

    public MineArenaRecord obtainReward(long accountId) {
        MineArenaRecord record = resolveReward(accountId);
        if (record.isResolvedRewardDelivered()) {
            throw MineArenaException.rewardAlreadyDelivered();
        }
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_毫仙石) < MineArenaConstants.PRICE_OBTAIN) {
            throw MineArenaException.insufficientXS();
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, MineArenaConstants.PRICE_OBTAIN, true, CurrencyConstants.PURPOSE_DECREMENT_抢占摇钱树领取奖励);
        CurrencyStack.listFromText(record.getResolvedReward()).forEach(stack -> {
            currencyService.increaseCurrency(accountId, stack.getCurrencyId(), stack.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_抢占摇钱树产出);
        });
        record.setResolvedRewardDelivered(true);

        MineArenaRewardObtainLog log = new MineArenaRewardObtainLog();
        log.setAccountId(accountId);
        log.setRewardText(record.getResolvedReward());
        log.setEventTime(new Date(timeProvider.currentTime()));
        mineArenaRewardObtainLogRepository.save(log);

        eventPublisher.publishEvent(new MineArenaRewardObtainEvent(this, accountId));

        return record;
    }

    public PitDetail wrapPitWithDetail(Pit pit) {
        ArenaRankInfoLoader rankInfoLoader = resourceContext.getByLoaderType(ArenaRankInfoLoader.class);
        ArenaRankInfo arenaRankInfo = rankInfoLoader.getByRanking((int) pit.getPosition());
        return new PitDetail(
                pit,
                challengeRelationMap.containsKey(pit.getPosition()) || challengeRelationMap.containsValue(pit.getPosition()),
                arenaRankInfo == null ? MineArenaConstants.OUTER_RANKING_CURRENCY_ID : arenaRankInfo.getCurrency(),
                arenaRankInfo == null ? MineArenaConstants.OUTER_RANKING_EFFICIENCY : arenaRankInfo.getFactor()
        );
    }

    public List<PitDetail> wrapPitsWithDetail(List<Pit> pits) {
        ArenaRankInfoLoader rankInfoLoader = resourceContext.getByLoaderType(ArenaRankInfoLoader.class);
        return pits.stream()
                .map(pit -> {
                    ArenaRankInfo arenaRankInfo = rankInfoLoader.getByRanking((int) pit.getPosition());
                    return new PitDetail(
                            pit,
                            challengeRelationMap.containsKey(pit.getPosition()) || challengeRelationMap.containsValue(pit.getPosition()),
                            arenaRankInfo == null ? MineArenaConstants.OUTER_RANKING_CURRENCY_ID : arenaRankInfo.getCurrency(),
                            arenaRankInfo == null ? MineArenaConstants.OUTER_RANKING_EFFICIENCY : arenaRankInfo.getFactor()
                    );
                })
                .collect(Collectors.toList());
    }

    private int getChallengePrice(Pit pit) {
        return pit.getChallengedCount() * 100 + 100;
    }

    private void logPitPositionChange(long accountId, long beforePosition, long afterPosition) {
        PitPositionChangeLog log = new PitPositionChangeLog();
        log.setAccountId(accountId);
        log.setBeforePosition(beforePosition);
        log.setAfterPosition(afterPosition);
        log.setEventTime(new Date(timeProvider.currentTime()));
        pitPositionChangeLogRepository.save(log);
    }

    public RankingRange getValidChallengingPositionRange(long position) {
        if (position <= 1) {
            return new RankingRange(0, 0);
        } else {
            return new RankingRange(Math.min((int) (position / Math.pow(Math.log(position), 0.2)), position - 3), position - 1);
        }
    }

    @Override
    public void dailyReset() {
        mineArenaRepository.resetChallengePoint(MineArenaConstants.CHALLENGE_POINT_INIT);
        pitRepository.resetChallengedCount();
    }

    @Scheduled(fixedDelay = 60_000)
    public void expireTimeoverChallenge() {
        long currentTime = timeProvider.currentTime();
        List<Long> expiredChallengeIds;
        synchronized (challengeMap) {
            expiredChallengeIds = challengeMap.entrySet().stream()
                    .filter(it -> it.getValue().getDeadline().getTime() < currentTime)
                    .map(it -> it.getKey())
                    .collect(Collectors.toList());
        }
        expiredChallengeIds.forEach(it -> {
            try {
                transactionTemplate.execute(status -> {
                    concludeChallenge(it, false);
                    return null;
                });
            } catch (TransactionException ex) {
                LOG.error("处理过期挑战时出现异常：{} {}", ex.getClass(), ex.getMessage());
            }
        });
    }

    @Value
    private static class Challenge {

        private long challengerPitPosition;
        private Date deadline;
        private int price;
    }
}
