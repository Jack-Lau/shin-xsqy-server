/*
 * Created 2019-1-21 16:16:01
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.activity.ActivityService;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.game.fuxingjianglin.resource.LuckyStarAwardInfo;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.quest.QuestRecord;
import cn.com.yting.kxy.web.quest.QuestRepository;
import cn.com.yting.kxy.web.quest.QuestService;
import java.time.DayOfWeek;
import java.util.ArrayList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class FxjlService implements InitializingBean, ResetTask {

    @Autowired
    private FxjlRepository fxjlRepository;
    @Autowired
    private FxjlSharedRepository fxjlSharedRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MailService mailService;
    @Autowired
    private QuestService questService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ActivityService activityService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;

    RandomSelector<Long> jackpot;

    private boolean available = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder jackpotBuilder = RandomSelector.<Long>builder();
        List<LuckyStarAwardInfo> jackpots = new ArrayList<>(resourceContext.getLoader(LuckyStarAwardInfo.class).getAll().values());
        jackpots.forEach((ssj) -> {
            if (ssj.getAward() != 0) {
                jackpotBuilder.add(ssj.getId(), 1.0);
            }
        });
        jackpot = jackpotBuilder.build(RandomSelectType.DEPENDENT);
        //
        fxjlSharedRepository.init(() -> {
            FxjlSharedRecord sharedRecord = new FxjlSharedRecord();
            sharedRecord.randomizeQuestIds();
            sharedRecord = randomizeLuckyInfoId(sharedRecord);
            return sharedRecord;
        });
        //
        if (timeProvider.today().getDayOfWeek() == DayOfWeek.SATURDAY || timeProvider.today().getDayOfWeek() == DayOfWeek.SUNDAY) {
            if (!available) {
                open();
            }
        } else {
            if (available) {
                close();
            }
        }
    }

    public FxjlRecord createRecord(long accountId) {
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 50) {
            throw KxyWebException.unknown("等级不足");
        }
        if (fxjlRepository.existsById(accountId)) {
            throw KxyWebException.unknown("记录已存在");
        }
        FxjlRecord record = new FxjlRecord();
        record.setAccountId(accountId);
        record.setAwardDelivered(false);
        return fxjlRepository.save(record);
    }

    public QuestRecord startQuest(long accountId, int index) {
        if (available) {
            FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecord();
            long questId = 0;
            switch (index) {
                case 0:
                    questId = sharedRecord.getQuestId_1();
                    break;
                case 1:
                    questId = sharedRecord.getQuestId_2();
                    break;
                case 2:
                    questId = sharedRecord.getQuestId_3();
                    break;
                case 3:
                    questId = sharedRecord.getQuestId_4();
                    break;
            }
            activityService.makeProgress(accountId, 158007, 3);
            return questService.startFxjlQuest(accountId, questId);
        } else {
            throw KxyWebException.unknown("活动未开启");
        }
    }

    public void resetQuest(long accountId, int index) {
        if (available) {
            FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecord();
            long questId = 0;
            switch (index) {
                case 0:
                    questId = sharedRecord.getQuestId_1();
                    break;
                case 1:
                    questId = sharedRecord.getQuestId_2();
                    break;
                case 2:
                    questId = sharedRecord.getQuestId_3();
                    break;
                case 3:
                    questId = sharedRecord.getQuestId_4();
                    break;
            }
            QuestRecord questRecord = questRepository.findById(accountId, questId).get();
            if (questRecord.getResults().equals("A")) {
                throw KxyWebException.unknown("任务状态不正确");
            }
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, KuaibiUnits.fromKuaibi(30), true, CurrencyConstants.PURPOSE_DECREMENT_提交货币的任务);
            questRepository.delete(questRecord);
        } else {
            throw KxyWebException.unknown("活动未开启");
        }
    }

    public AwardResult obtainAward(long accountId) {
        if (available) {
            FxjlRecord record = fxjlRepository.findByIdForWrite(accountId).get();
            if (record.isAwardDelivered()) {
                throw KxyWebException.unknown("已经领取过");
            }
            if (!isAwardAvailable(accountId)) {
                throw KxyWebException.unknown("未达到领取条件");
            }

            FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecord();
            LuckyStarAwardInfo luckyStarAwardInfo = resourceContext.getLoader(LuckyStarAwardInfo.class).get(sharedRecord.getTodayLuckyInfoId());
            AwardResult awardResult = awardService.processAward(accountId, luckyStarAwardInfo.getAward());

            record.setAwardDelivered(true);

            return awardResult;
        } else {
            throw KxyWebException.unknown("活动未开启");
        }
    }

    @Override
    public void dailyReset() {
        if (available) {
            FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecordForWrite();
            sharedRecord.randomizeQuestIds();
            sharedRecord = randomizeLuckyInfoId(sharedRecord);

            List<FxjlRecord> allRecords = fxjlRepository.findAllForWrite();

            allRecords.forEach(record -> {
                if (!record.isAwardDelivered() && isAwardAvailable(record.getAccountId())) {
                    int days = Period.between(LocalDate.of(2019, Month.JANUARY, 25), timeProvider.currentOffsetDateTime().minusHours(1).toLocalDate()).getDays();
                    LuckyStarAwardInfo luckyStarAwardInfo = resourceContext.getLoader(LuckyStarAwardInfo.class).get(days + 1);
                    new MailSendingRequest()
                            .to(record.getAccountId())
                            .template(62)
                            .attachment(luckyStarAwardInfo.getMailItem())
                            .commit(mailService);
                } else {
                    record.setAwardDelivered(false);
                }
            });

            questRepository.deleteFxjlQuests();
        }
    }

    @RegisterScheduledTask(cronExpression = "0 1 0 ? * SAT", executeIfNew = true)
    public void open() {
        available = true;
    }

    @RegisterScheduledTask(cronExpression = "0 59 23 ? * SUN", executeIfNew = true)
    public void close() {
        if (available) {
            dailyReset();
        }
        available = false;
    }
    
    public boolean isAvailable() {
        return available;
    }

    private FxjlSharedRecord randomizeLuckyInfoId(FxjlSharedRecord sharedRecord) {
        sharedRecord.setTodayLuckyInfoId(jackpot.getSingle());
        return sharedRecord;
    }

    private boolean isAwardAvailable(long accountId) {
        FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecord();
        long successCount = questRepository.findAllById(
                Stream.of(sharedRecord.getQuestId_1(), sharedRecord.getQuestId_2(), sharedRecord.getQuestId_3(), sharedRecord.getQuestId_4())
                        .map(questId -> new QuestRecord.QuestRecordPK(accountId, questId))
                        .collect(Collectors.toList())
        ).stream()
                .filter(it -> it.getResults().equals("A"))
                .count();
        return successCount >= 3;
    }
}
