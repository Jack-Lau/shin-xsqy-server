/*
 * Created 2018-12-12 11:11:31
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.battle.BattleResult.BattleStatistics;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.Unit.UnitType;
import cn.com.yting.kxy.battle.event.BattleEvent.BattleEventType;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.battle.BattleService;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.battle.multiplayer.MultiplayerBattleService;
import cn.com.yting.kxy.web.battle.multiplayer.MultiplayerBattleSession;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.game.mingjiandahui.resource.KingBattleEverydayAward;
import cn.com.yting.kxy.web.game.mingjiandahui.resource.KingBattleRank;
import cn.com.yting.kxy.web.game.mingjiandahui.resource.KingBattleRankLoader;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.ranking.RankingConstants;
import cn.com.yting.kxy.web.ranking.RankingRecord;
import cn.com.yting.kxy.web.ranking.RankingRepository;
import cn.com.yting.kxy.web.ranking.RankingService;
import com.google.common.collect.ImmutableMap;
import io.github.azige.mgxy.event.EventHandler;
import lombok.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class MjdhService implements InitializingBean, ResetTask {

    @Autowired
    private MjdhSeasonRepository mjdhSeasonRepository;
    @Autowired
    private MjdhPlayerRepository mjdhPlayerRepository;
    @Autowired
    private MjdhBattleLogRepository mjdhBattleLogRepository;
    @Autowired
    private MjdhWinnerRepository mjdhWinnerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private RankingRepository rankingRepository;
    @Autowired
    private MjdhDummyRepository mjdhDummyRepository;

    @Autowired
    @Lazy
    private MjdhService self;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RankingService rankingService;
    @Autowired
    private BattleService battleService;
    @Autowired
    private MultiplayerBattleService multiplayerBattleService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Object globalLock = new Object();
    private LinkedList<MjdhMatchRequest> matchQueue = new LinkedList<>();
    private final List<MultiplayerBattleSession> activeSessions = new ArrayList<>();

    private long currentSeasonId;

    @Override
    public void afterPropertiesSet() throws Exception {
        MjdhSeason currentSeason = mjdhSeasonRepository.findTopByOrderByIdDesc().orElse(null);
        if (currentSeason == null) {
            currentSeason = createAndSaveSeason(timeProvider.currentOffsetDateTime());
        }
        currentSeasonId = currentSeason.getId();
    }

    public long getCurrentSeasonId() {
        return currentSeasonId;
    }

    public MjdhPlayerRecord createRecord(long accountId) {
        synchronized (globalLock) {
            if (mjdhPlayerRepository.existsById(new MjdhPlayerRecord.PK(currentSeasonId, accountId))) {
                throw KxyWebException.unknown("已经存在记录");
            }
            Player player = playerRepository.findById(accountId).get();
            if (player.getPlayerLevel() < 50) {
                throw KxyWebException.unknown("等级不够");
            }
            MjdhPlayerRecord playerRecord = new MjdhPlayerRecord();
            playerRecord.setAccountId(accountId);
            playerRecord.setSeasonId(currentSeasonId);

            int grade = MjdhConstants.GRADE_青铜五;
            MjdhPlayerRecord previousSeasonPlayerRecord = mjdhPlayerRepository.findByIdForWrite(currentSeasonId - 1, accountId).orElse(null);
            if (previousSeasonPlayerRecord != null) {
                grade = resourceContext.getLoader(KingBattleRank.class).get(previousSeasonPlayerRecord.getCappedGrade()).getNewSeasonRank();
            }
            playerRecord.setGrade(grade);

            return mjdhPlayerRepository.save(playerRecord);
        }
    }

    public void startMatch(long accountId) {
        if (!MjdhConstants.VALID_PERIOD_GAME.isValid(timeProvider.currentInstant()) && !MjdhConstants.VALID_PERIOD_GAME_EXTRA.isValid(timeProvider.currentInstant())) {
            throw KxyWebException.unknown("当前不在有效时间内");
        }
        synchronized (globalLock) {
            MjdhPlayerRecord playerRecord = mjdhPlayerRepository.findByIdForWrite(currentSeasonId, accountId).orElseThrow(() -> KxyWebException.unknown("当季记录不存在"));
            if (matchQueue.stream().anyMatch(it -> it.getPlayerRecord().getAccountId() == accountId)) {
                throw KxyWebException.unknown("已经在排队");
            }
            if (activeSessions.stream()
                    .flatMap(it -> it.getAgents().stream())
                    .anyMatch(it -> it.getAccountId() == accountId)) {
                throw KxyWebException.unknown("已经在战斗中");
            }
            if (playerRecord.getCappedGrade() < MjdhConstants.GRADE_白银五) {
                @Value
                class SimpleLog {

                    boolean lost;
                    Date eventTime;
                }
                SimpleLog[] logs = Stream.concat(
                        mjdhBattleLogRepository.findTop2ByWinnerAccountIdOrderByEventTimeDesc(accountId).stream()
                                .map(it -> new SimpleLog(false, it.getEventTime())),
                        mjdhBattleLogRepository.findTop2ByLoserAccountIdOrderByEventTimeDesc(accountId).stream()
                                .map(it -> new SimpleLog(true, it.getEventTime())))
                        .sorted(Comparator.comparing(SimpleLog::getEventTime).reversed())
                        .limit(2)
                        .toArray(SimpleLog[]::new);
                if (logs.length >= 2 && Stream.of(logs).allMatch(SimpleLog::isLost)) {
                    BattleSession session = startSinglePlayerBattle(accountId);
                    eventPublisher.publishEvent(new SinglePlayerMjdhBattleStartedEvent(this, accountId, session));
                    return;
                }
            }
            MjdhMatchRequest request = new MjdhMatchRequest(playerRecord, timeProvider.currentInstant());
            matchQueue.add(request);
        }
    }

    public void cancelMatch(long accountId) {
        synchronized (globalLock) {
            boolean removed = matchQueue.removeIf(it -> it.getPlayerRecord().getAccountId() == accountId);
            if (!removed) {
                throw KxyWebException.unknown("当前未在排队");
            }
        }
    }

    @Scheduled(cron = "0/3 * 12 * * *")
    public void update() {
        self.tryMatch();
    }

    @Scheduled(cron = "0 0 13 * * *")
    public void cleanMatchRequest() {
        synchronized (globalLock) {
            matchQueue.clear();
        }
    }

    @Scheduled(cron = "0/3 * 18 * * *")
    public void update_night() {
        self.tryMatch();
    }

    @Scheduled(cron = "0 0 19 * * *")
    public void cleanMatchRequest_night() {
        synchronized (globalLock) {
            matchQueue.clear();
        }
    }

    public void tryMatch() {
        synchronized (globalLock) {
            LinkedList<MjdhMatchRequest> newQueue = new LinkedList<>();
            while (!matchQueue.isEmpty()) {
                MjdhMatchRequest request = matchQueue.poll();
                boolean matched = false;
                if (Duration.between(request.getEventTime(), timeProvider.currentInstant()).getSeconds() < 6) {
                    List<MjdhMatchRequest> matchedList = matchQueue.stream()
                            .filter(it -> isMatchable(request, it))
                            .collect(Collectors.toList());
                    if (matchedList.size() >= 10) {
                        MjdhMatchRequest selectedOpponent = matchedList.get(RandomProvider.getRandom().nextInt(matchedList.size()));
                        MultiplayerBattleSession session = startMultiplayerBattle(request.getPlayerRecord().getAccountId(), selectedOpponent.getPlayerRecord().getAccountId());
                        activeSessions.add(session);
                        eventPublisher.publishEvent(new MultiplayerMjdhBattleStartedEvent(this, session));
                        matchQueue.remove(selectedOpponent);
                        matched = true;
                    }
                } else {
                    for (int i = 0; i <= 3; i++) {
                        int X = i;
                        List<MjdhMatchRequest> matchedList = matchQueue.stream()
                                .filter(it -> isMatchable(request, it, X))
                                .collect(Collectors.toList());
                        if (matchedList.size() >= 3) {
                            MjdhMatchRequest selectedOpponent = matchedList.get(RandomProvider.getRandom().nextInt(matchedList.size()));
                            MultiplayerBattleSession session = startMultiplayerBattle(request.getPlayerRecord().getAccountId(), selectedOpponent.getPlayerRecord().getAccountId());
                            activeSessions.add(session);
                            eventPublisher.publishEvent(new MultiplayerMjdhBattleStartedEvent(this, session));
                            matchQueue.remove(selectedOpponent);
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        BattleSession session = startSinglePlayerBattle(request.getPlayerRecord().getAccountId());
                        eventPublisher.publishEvent(new SinglePlayerMjdhBattleStartedEvent(this, request.getPlayerRecord().getAccountId(), session));
                        matched = true;
                    }
                }
                if (!matched) {
                    newQueue.add(request);
                }
            }
            matchQueue = newQueue;
        }
    }

    private boolean isMatchable(MjdhMatchRequest one, MjdhMatchRequest other) {
        return isMatchable(one, other, 0);
    }

    private boolean isMatchable(MjdhMatchRequest one, MjdhMatchRequest other, int X) {
        KingBattleRankLoader loader = resourceContext.getByLoaderType(KingBattleRankLoader.class);
        int upperBound, lowerBound;
        upperBound = lowerBound = one.getPlayerRecord().getCappedGrade();
        for (int i = 0; i < 2 + X; i++) {
            upperBound = loader.nextRank(upperBound);
            lowerBound = loader.previousRank(lowerBound);
        }
        int otherGrade = other.getPlayerRecord().getCappedGrade();
        return lowerBound < otherGrade && otherGrade < upperBound;
    }

    private BattleSession startSinglePlayerBattle(long accountId) {
        List<MjdhDummyRecord> robots = mjdhDummyRepository.findAll();
        MjdhPlayerRecord opponentRecord = null;
        MjdhPlayerRecord playerRecord = mjdhPlayerRepository.findById(currentSeasonId, accountId).orElseThrow(() -> KxyWebException.unknown("当季记录不存在"));
        List<MjdhPlayerRecord> opponents = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            for (int j = playerRecord.getGrade() - i; j <= playerRecord.getGrade() + i; j++) {
                List<MjdhPlayerRecord> records = mjdhPlayerRepository.findByGrade(j);
                for (MjdhPlayerRecord record : records) {
                    if (record.getAccountId() != accountId) {
                        opponents.add(record);
                    }
                }
            }
            if (opponents.size() > 0) {
                opponentRecord = opponents.get(RandomProvider.getRandom().nextInt(opponents.size()));
                break;
            }
        }
        //
        long opponentAccountId = opponentRecord == null ? robots.get(RandomProvider.getRandom().nextInt(robots.size())).getAccountId() : opponentRecord.getAccountId();
        return battleService.startAsyncPVP(accountId, Collections.singletonList(opponentAccountId), false, false, Collections.singletonList(EventHandler.of(event -> {
            boolean playerWin = Objects.equals(event.getBattleDirector().getBattleResult().getStatistics().getWinStance(), Unit.Stance.STANCE_RED);
            self.onSiglePlayerBattleEnd(accountId, opponentAccountId, playerWin);
        }, BattleEventType.BATTLE_END, 0)));
    }

    private MultiplayerBattleSession startMultiplayerBattle(long accountId, long anotherAccountId) {
        return multiplayerBattleService.startBattle(Collections.singletonList(accountId),
                Collections.singletonList(anotherAccountId),
                true,
                false,
                self::onMultiplayerBattleEnd
        );
    }

    public void onMultiplayerBattleEnd(MultiplayerBattleSession session) {
        synchronized (globalLock) {
            activeSessions.remove(session);
            if (!session.isFailedBeforeStart()) {
                BattleStatistics statistics = session.getBattleDirector().getBattleResult().getStatistics();
                long redPartyAccountId = statistics.getRedParty().getUnitMap().values().stream()
                        .filter(it -> Objects.equals(it.getType(), UnitType.TYPE_PLAYER))
                        .map(Unit::getSourceId)
                        .findAny().get();
                long bluePartyAccountId = statistics.getBlueParty().getUnitMap().values().stream()
                        .filter(it -> Objects.equals(it.getType(), UnitType.TYPE_PLAYER))
                        .map(Unit::getSourceId)
                        .findAny().get();
                MjdhBattleLog battleLog = new MjdhBattleLog();
                battleLog.setEventTime(new Date(timeProvider.currentTime()));
                if (Objects.equals(session.getBattleDirector().getBattleResult().getStatistics().getWinStance(), Unit.Stance.STANCE_RED)) {
                    onBattleEndForWinner(redPartyAccountId, battleLog);
                    onBattleEndForLoser(bluePartyAccountId, battleLog);
                } else {
                    onBattleEndForWinner(bluePartyAccountId, battleLog);
                    onBattleEndForLoser(redPartyAccountId, battleLog);
                }
                battleLog = mjdhBattleLogRepository.saveAndFlush(battleLog);
                eventPublisher.publishEvent(new MjdhBattleEndEvent(this, Arrays.asList(redPartyAccountId, bluePartyAccountId), battleLog));
            }
        }
    }

    public void onSiglePlayerBattleEnd(long playerAccountId, long dummyAccountId, boolean playerWin) {
        synchronized (globalLock) {
            MjdhBattleLog battleLog = new MjdhBattleLog();
            battleLog.setEventTime(new Date(timeProvider.currentTime()));
            if (playerWin) {
                onBattleEndForWinner(playerAccountId, battleLog);
                battleLog.setLoserAccountId(dummyAccountId);
            } else {
                onBattleEndForLoser(playerAccountId, battleLog);
                battleLog.setWinnerAccountId(dummyAccountId);
            }
            battleLog = mjdhBattleLogRepository.saveAndFlush(battleLog);
            eventPublisher.publishEvent(new MjdhBattleEndEvent(this, Arrays.asList(playerAccountId), battleLog));
        }
    }

    private void onBattleEndForWinner(long winnerAccountId, MjdhBattleLog battleLog) {
        MjdhPlayerRecord playerRecord = mjdhPlayerRepository.findByIdForWrite(currentSeasonId, winnerAccountId).get();
        battleLog.setWinnerAccountId(winnerAccountId);
        battleLog.setWinnerBeforeGrade(playerRecord.getGrade());
        playerRecord.increaseGrade();
        if (playerRecord.getGrade() < MjdhConstants.GRADE_王者 && playerRecord.getConsecutiveWinCount() >= 3) {
            playerRecord.increaseGrade();
        }
        battleLog.setWinnertAftereGrade(playerRecord.getGrade());
        awardService.processAward(playerRecord.getAccountId(), 3114);
        playerRecord.increaseConsecutiveWinCount();
        if (playerRecord.getConsecutiveWinCount() % 10 == 0) {
            Player player = playerRepository.findById(playerRecord.getAccountId()).get();
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            3200036,
                            ImmutableMap.of("playerName", player.getPlayerName(), "consecutiveWin", playerRecord.getConsecutiveWinCount())
                    )
            );
        }
        if (playerRecord.getGrade() == MjdhConstants.GRADE_王者) {
            Player player = playerRepository.findById(playerRecord.getAccountId()).get();
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            3200037,
                            ImmutableMap.of("playerName", player.getPlayerName())
                    )
            );
        }
        playerRecord.setDailyFirstWin(true);
        playerRecord.increaseDailyConsecutiveWinCount();
        if (playerRecord.getDailyConsecutiveWinCount() >= 5) {
            playerRecord.setDailyConsecutiveWinAwardAvailable(true);
        }
        playerRecord.increaseDailyBattleCount();

        rankingService.updateRankingValue(RankingConstants.RANKING_ID_名剑大会, winnerAccountId, winnerAccountId, -playerRecord.getGrade());
    }

    private void onBattleEndForLoser(long loserAccountId, MjdhBattleLog battleLog) {
        MjdhPlayerRecord playerRecord = mjdhPlayerRepository.findByIdForWrite(currentSeasonId, loserAccountId).get();
        battleLog.setLoserAccountId(loserAccountId);
        battleLog.setLoserBeforeGrade(playerRecord.getGrade());
        if (playerRecord.getGrade() > MjdhConstants.GRADE_王者) {
            playerRecord.decreaseGrade();
        } else {
            KingBattleRank kingBattleRank = resourceContext.getLoader(KingBattleRank.class).get(playerRecord.getCappedGrade());
            if (kingBattleRank.getProtectRank() != 1) {
                playerRecord.decreaseGrade();
            }
        }
        battleLog.setLoserAfterGrade(playerRecord.getGrade());
        awardService.processAward(playerRecord.getAccountId(), 3115);
        playerRecord.setConsecutiveWinCount(0);
        playerRecord.increaseDailyBattleCount();
        playerRecord.setDailyConsecutiveWinCount(0);

        rankingService.updateRankingValue(RankingConstants.RANKING_ID_名剑大会, loserAccountId, loserAccountId, -playerRecord.getGrade());
    }

    private List<CurrencyStack> obtainAward(
            long accountId,
            Predicate<MjdhPlayerRecord> deliveredChecker,
            Predicate<MjdhPlayerRecord> conditionChecker,
            BiConsumer<MjdhPlayerRecord, Boolean> deliveredSetter,
            long kingBattleEverydayAwardId
    ) {
        synchronized (globalLock) {
            MjdhPlayerRecord playerRecord = mjdhPlayerRepository.findByIdForWrite(currentSeasonId, accountId).orElseThrow(() -> KxyWebException.notFound("玩家记录不存在"));
            if (deliveredChecker.test(playerRecord)) {
                throw KxyWebException.unknown("已经领取过");
            }
            if (!conditionChecker.test(playerRecord)) {
                throw KxyWebException.unknown("未达到领取条件");
            }
            deliveredSetter.accept(playerRecord, true);

            KingBattleEverydayAward kingBattleEverydayAward = resourceContext.getLoader(KingBattleEverydayAward.class).get(kingBattleEverydayAwardId);
            List<CurrencyStack> currencyStacks = kingBattleEverydayAward.getCurrencyStacks();
            currencyStacks.forEach(stack -> {
                currencyService.increaseCurrency(accountId, stack.getCurrencyId(), stack.getAmount());
            });
            return currencyStacks;
        }
    }

    public List<CurrencyStack> obtainDailyFirstWinAward(long accountId) {
        return obtainAward(
                accountId,
                MjdhPlayerRecord::isDailyFirstWinAwardDelivered,
                MjdhPlayerRecord::isDailyFirstWin,
                MjdhPlayerRecord::setDailyFirstWinAwardDelivered,
                1
        );
    }

    public List<CurrencyStack> obtainDailyConsecutiveWinAward(long accountId) {
        return obtainAward(
                accountId,
                MjdhPlayerRecord::isDailyConsecutiveWinAwardDelivered,
                MjdhPlayerRecord::isDailyConsecutiveWinAwardAvailable,
                MjdhPlayerRecord::setDailyConsecutiveWinAwardDelivered,
                2
        );
    }

    public List<CurrencyStack> obtainDailyTenBattleAward(long accountId) {
        return obtainAward(
                accountId,
                MjdhPlayerRecord::isDailyTenBattleAwardDelivered,
                it -> it.getDailyBattleCount() >= 10,
                MjdhPlayerRecord::setDailyTenBattleAwardDelivered,
                3
        );
    }

    public boolean isAvailable() {
        return MjdhConstants.VALID_PERIOD_GAME.isValid(timeProvider.currentInstant()) || MjdhConstants.VALID_PERIOD_GAME_EXTRA.isValid(timeProvider.currentInstant());
    }

    @Override
    public void dailyReset() {
        KingBattleEverydayAward kingBattleEverydayAward1 = resourceContext.getLoader(KingBattleEverydayAward.class).get(1);
        KingBattleEverydayAward kingBattleEverydayAward2 = resourceContext.getLoader(KingBattleEverydayAward.class).get(2);
        KingBattleEverydayAward kingBattleEverydayAward3 = resourceContext.getLoader(KingBattleEverydayAward.class).get(3);
        synchronized (globalLock) {
            List<MjdhPlayerRecord> allCurrentSeasonRecords = mjdhPlayerRepository.findBySeasonId(currentSeasonId);
            allCurrentSeasonRecords.forEach(record -> {
                if (record.isDailyFirstWin() && !record.isDailyFirstWinAwardDelivered()) {
                    MailSendingRequest.create()
                            .to(record.getAccountId())
                            .template(kingBattleEverydayAward1.getMail())
                            .attachment(kingBattleEverydayAward1.getCurrencyStacks())
                            .commit(mailService);
                }
                if (record.isDailyConsecutiveWinAwardAvailable() && !record.isDailyConsecutiveWinAwardDelivered()) {
                    MailSendingRequest.create()
                            .to(record.getAccountId())
                            .template(kingBattleEverydayAward2.getMail())
                            .attachment(kingBattleEverydayAward2.getCurrencyStacks())
                            .commit(mailService);
                }
                if (record.getDailyBattleCount() >= 10 && !record.isDailyTenBattleAwardDelivered()) {
                    MailSendingRequest.create()
                            .to(record.getAccountId())
                            .template(kingBattleEverydayAward3.getMail())
                            .attachment(kingBattleEverydayAward3.getCurrencyStacks())
                            .commit(mailService);
                }

                record.setDailyFirstWin(false);
                record.setDailyConsecutiveWinCount(0);
                record.setDailyConsecutiveWinAwardAvailable(false);
                record.setDailyBattleCount(0);
                record.setDailyFirstWinAwardDelivered(false);
                record.setDailyConsecutiveWinAwardDelivered(false);
                record.setDailyTenBattleAwardDelivered(false);
            });

            MjdhSeason currentSeason = mjdhSeasonRepository.findByIdForWrite(currentSeasonId).get();
            OffsetDateTime referenceTime = timeProvider.currentOffsetDateTime().plusHours(1);
            if (referenceTime.isAfter(TimeUtils.toOffsetTime(currentSeason.getEndTime()))) {
                ResourceLoader<KingBattleRank> loader = resourceContext.getLoader(KingBattleRank.class);
                allCurrentSeasonRecords.forEach(record -> {
                    KingBattleRank kingBattleRank = loader.get(record.getCappedGrade());
                    MailSendingRequest.create()
                            .to(record.getAccountId())
                            .template(52)
                            .attachment(kingBattleRank.getCurrencyStacks())
                            .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_名剑大会1V1排行榜)
                            .commit(mailService);
                });

                List<RankingRecord> rankingRecords = rankingRepository.findByRankingId(RankingConstants.RANKING_ID_名剑大会);
                for (int i = 0; i < 3 && i < rankingRecords.size(); i++) {
                    MjdhWinnerRecord winnerRecord = new MjdhWinnerRecord();
                    winnerRecord.setSeasonId(currentSeasonId);
                    winnerRecord.setRanking(i + 1);
                    winnerRecord.setAccountId(rankingRecords.get(i).getAccountId());
                    mjdhWinnerRepository.save(winnerRecord);
                }

                rankingService.requestResolveAward(RankingConstants.RANKING_ID_名剑大会);

                currentSeasonId = createAndSaveSeason(referenceTime).getId();

                rankingRepository.resetRankingValueByRankingId(RankingConstants.RANKING_ID_名剑大会);
            }
        }
    }

    private MjdhSeason createAndSaveSeason(OffsetDateTime referenceTime) {
        MjdhSeason season = new MjdhSeason();
        OffsetDateTime startOfMonth = referenceTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        OffsetDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        season.setStartTime(Date.from(startOfMonth.toInstant()));
        season.setEndTime(Date.from(endOfMonth.toInstant()));
        return mjdhSeasonRepository.saveAndFlush(season);
    }
}
