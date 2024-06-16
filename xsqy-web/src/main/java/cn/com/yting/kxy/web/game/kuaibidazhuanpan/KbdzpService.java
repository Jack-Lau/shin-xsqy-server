/*
 * Created 2018-7-6 16:46:10
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;

import cn.com.yting.kxy.core.AlphaDigitCodeGenerator;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.pool.PoolValueHolder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountCreatedEvent;
import cn.com.yting.kxy.web.account.AccountRepository;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.resource.KCWheel;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.resource.KCWheelLoader;
import cn.com.yting.kxy.web.invitation.InviterRecordCreatedEvent;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableMap;

import java.util.Optional;

import cn.com.yting.kxy.web.quest.QuestRepository;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class KbdzpService implements InitializingBean, ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(KbdzpService.class);

    @Autowired
    private KbdzpRepository kbdzpRepository;
    @Autowired
    private KbdzpSharedRepository kbdzpSharedRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MailService mailService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final AlphaDigitCodeGenerator codeGenerator = new AlphaDigitCodeGenerator(4);
    private final Queue<KbdzpAwardLog> latestInterestingAwards = EvictingQueue.create(10);

    private boolean fever = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        kbdzpSharedRepository.init(new KbdzpSharedRecord());
        LOG.info("欢乐大转盘检测到今天星期几是 {}", timeProvider.today().getDayOfWeek());
        if (timeProvider.today().getDayOfWeek() == DayOfWeek.SATURDAY || timeProvider.today().getDayOfWeek() == DayOfWeek.SUNDAY) {
            if (!fever) {
                open();
            }
        } else {
            if (fever) {
                close();
            }
        }
    }

    public KbdzpRecoverResult recoverEnergy(long accountId) {
        if (!playerRepository.existsById(accountId)) {
            throw KxyWebException.unknown("角色不存在");
        }
        KbdzpRecord record = kbdzpRepository.findByIdForWrite(accountId);
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_能量);

        long timeInterval = getRecoverTimeInterval(record);
        long currentTime = timeProvider.currentTime();
        long recoverRefTime = record.getRecoverRefTime().getTime();
        int energyToRecover = 0;
//        energyToRecover = (int) ((currentTime - recoverRefTime) / timeInterval);
        if (energyToRecover <= 0) {
            return new KbdzpRecoverResult(record, currencyRecord, recoverRefTime + timeInterval - currentTime);
        }

        increaseEnergy(accountId, energyToRecover, false);
        record.setRecoverRefTime(new Date(recoverRefTime + timeInterval * energyToRecover));
        kbdzpRepository.save(record);

        return new KbdzpRecoverResult(record, currencyRecord, record.getRecoverRefTime().getTime() + timeInterval - currentTime);
    }

    private void increaseEnergy(long accountId, int value, boolean allowOverLimit) {
        if (allowOverLimit) {
            currencyService.increaseCurrency(accountId, CurrencyConstants.ID_能量, value);
        } else {
            CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_能量);
            if (currencyRecord.getAmount() < KbdzpConstants.ENERGY_RECOVER_MAX_VALUE) {
                long energyToIncrease = Math.min(value, KbdzpConstants.ENERGY_RECOVER_MAX_VALUE - currencyRecord.getAmount());
                currencyService.increaseCurrency(accountId, CurrencyConstants.ID_能量, energyToIncrease);
            }
        }
    }

    public void addExtraEnergy(long accountId, int value) {
        increaseEnergy(accountId, value, true);
    }

    private KbdzpRecord enableBooster(
            long accountId,
            Function<KbdzpRecord, Boolean> enabledGetter,
            String activationCode,
            String actualActivationCode,
            BiConsumer<KbdzpRecord, Boolean> enabledSetter
    ) {
        KbdzpRecord record = recoverEnergy(accountId).getRecord();
        if (enabledGetter.apply(record)) {
            throw KbdzpException.boosterAlreadyEnabled();
        }
        if (activationCode == null || !activationCode.equalsIgnoreCase(actualActivationCode)) {
            throw KbdzpException.acodeNotValid();
        }

        enabledSetter.accept(record, true);
        increaseEnergy(accountId, 1, false);
        record.setRecoverRefTime(new Date(timeProvider.currentTime()));
        kbdzpRepository.save(record);

        return record;
    }

    public KbdzpRecord enableBooster1(long accountId, String activationCode) {
        return enableBooster(
                accountId,
                KbdzpRecord::isBooster1,
                activationCode,
                kbdzpSharedRepository.getTheRecord().getBooster1ActivationCode(),
                KbdzpRecord::setBooster1
        );
    }

    public KbdzpRecord enableBooster2(long accountId, String activationCode) {
        return enableBooster(
                accountId,
                KbdzpRecord::isBooster2,
                activationCode,
                kbdzpSharedRepository.getTheRecord().getBooster2ActivationCode(),
                KbdzpRecord::setBooster2
        );
    }

    public KbdzpRecord obtainInviteeBonus(long accountId) {
        KbdzpRecord record = kbdzpRepository.findByIdForWrite(accountId);
        if (!record.isInviteeBonusAvailable() || record.isInviteeBonusDelivered()) {
            throw KbdzpException.inviteeBonusNotAvailable();
        }

        increaseEnergy(accountId, KbdzpConstants.INVITEE_BONUS, true);
        record.setInviteeBonusDelivered(true);
        kbdzpRepository.save(record);

        return record;
    }

    private long getRecoverTimeInterval(KbdzpRecord record) {
        int level = 0;
        if (record.isBooster1()) {
            level++;
        }
        if (record.isBooster2()) {
            level++;
        }
        return KbdzpConstants.RECOVER_TIME_INTERVALS[level];
    }

    public KbdzpRecord makeTurn(long accountId) {
        if (!playerRepository.existsById(accountId)) {
            throw KxyWebException.unknown("角色不存在");
        }
        if (questRepository.findById(accountId, 700040)
                .map(quest -> !quest.getQuestStatus().equals(QuestStatus.COMPLETED))
                .orElse(true)) {
            throw KxyWebException.unknown("未达到前置任务条件");
        }
        Optional<Player> player = playerRepository.findById(accountId);
        KbdzpRecord record = kbdzpRepository.findByIdForWrite(accountId);
        if (record.getPendingAward() != null) {
            throw KbdzpException.pendingAwardExisted();
        }
        if (record.getTodayTurnCount() >= KbdzpConstants.MAX_TURN_COUNT_PER_DAY) {
            throw KbdzpException.todayTurnCountReachLimit();
        }
        long energy = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_能量).getAmount();
        if (energy < KbdzpConstants.ENERGY_COST_PER_TURN) {
            throw KbdzpException.insufficientEnergy((int) energy);
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_能量, KbdzpConstants.ENERGY_COST_PER_TURN);
        record.resetKuaibiPoolIfNecessary(timeProvider.currentTime());
        KbdzpSharedRecord kbdzpSharedRecord = kbdzpSharedRepository.getTheRecordForWrite();
        PoolValueHolder valueHolder = new PoolValueHolder(kbdzpSharedRecord.getPublicPool(), record.getKuaibiPool());

        KCWheel kcw;
        if (player.get().getPlayerLevel() < KbdzpConstants.NORMAL_KC_PROBABILITY_PLAYER_LEVEL
                || player.get().getFc() < KbdzpConstants.NORMAL_KC_PROBABILITY_PLAYER_FC) {
            kcw = (KCWheel) resourceContext.getByLoaderType(KCWheelLoader.class)
                    .getLowLevelSelector()
                    .getSingle(ImmutableMap.of(CurrencyConstants.ID_毫仙石, valueHolder))
                    .getPayload();
        } else {
            kcw = (KCWheel) resourceContext.getByLoaderType(KCWheelLoader.class)
                    .getNormalSelector()
                    .getSingle(ImmutableMap.of(CurrencyConstants.ID_毫仙石, valueHolder))
                    .getPayload();
        }

        record.setKuaibiPool(valueHolder.getRemainPersonalPoolAmount());
        kbdzpSharedRecord.setPublicPool(valueHolder.getRemainTotalPoolAmount());

        record.setPendingAward(kcw.getId());
        record.setTodayTurnCount(record.getTodayTurnCount() + 1);
        record.setTotalTurnCount(record.getTotalTurnCount() + 1);

        eventPublisher.publishEvent(new KbdzpMadeTurnEvent(this, record));

        return record;
    }

    public KbdzpRecord obtainAward(long accountId) {
        if (!playerRepository.existsById(accountId)) {
            throw KxyWebException.unknown("角色不存在");
        }
        KbdzpRecord record = kbdzpRepository.findByIdForWrite(accountId);
        if (record.getPendingAward() == null) {
            throw KbdzpException.pendingAwardNotExisted();
        }

        KCWheel kcw = resourceContext.getLoader(KCWheel.class).get(record.getPendingAward());
        currencyService.increaseCurrency(accountId, kcw.getCurrencyId(), getAwardAmount(accountId, kcw.getAmount()), CurrencyConstants.PURPOSE_INCREMENT_块币大转盘);
        record.setPendingAward(null);
        if (kcw.getBroadcastId() != null) {
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    kcw.getBroadcastId(),
                    ImmutableMap.of("playerName", playerRepository.findById(accountId).map(Player::getPlayerName).orElse("角色名不存在"))
            ));
        }
        if (kcw.isNeedToShowResult()) {
            String playerName = playerRepository.findById(accountId)
                    .map(Player::getPlayerName)
                    .orElse(null);
            synchronized (latestInterestingAwards) {
                latestInterestingAwards.offer(new KbdzpAwardLog(playerName, kcw.getId(), timeProvider.currentTime()));
            }
        }

//        if (kcw.getCurrencyId() == CurrencyConstants.ID_毫块币) {
//            long beforeGainMilliKC = record.getTotalGainMilliKC();
//            record.setTotalGainMilliKC(beforeGainMilliKC + kcw.getAmount());
//            if (beforeGainMilliKC < KbdzpConstants.GET_TITLE_TOTAL_GAIN_MILLI_KC
//                    && record.getTotalGainMilliKC() >= KbdzpConstants.GET_TITLE_TOTAL_GAIN_MILLI_KC) {
//                MailSendingRequest.create()
//                        .template(KbdzpConstants.TITLE_MAIL_ID)
//                        .attachment(Collections.singletonList(new CurrencyStack(KbdzpConstants.TITLE_CURRENCY_ID, 1)))
//                        .to(record.getAccountId())
//                        .commit(mailService);
//            }
//        }
        if (record.getTotalTurnCount() == KbdzpConstants.GET_TITLE_TOTAL_TURN_COUNT) {
            MailSendingRequest.create()
                    .template(KbdzpConstants.TITLE_MAIL_ID)
                    .attachment(Collections.singletonList(new CurrencyStack(KbdzpConstants.TITLE_CURRENCY_ID, 1)))
                    .to(record.getAccountId())
                    .commit(mailService);
        }
        //
        return record;
    }

    private long getAwardAmount(long accountId, long rawAmount) {
        long result = (long) (rawAmount * (1 + (playerRepository.getOne(accountId).getPlayerLevel() / 10) * 0.3));
        if (fever) {
            result = result * 3;
        }
        return result;
    }

    public Collection<KbdzpAwardLog> getLatestInterestingAwards() {
        synchronized (latestInterestingAwards) {
            return new ArrayList<>(latestInterestingAwards);
        }
    }

    public void createRecordsForInexistent() {
        accountRepository.findAll().stream()
                .map(Account::getId)
                .forEach(id -> {
                    if (!kbdzpRepository.existsById(id)) {
                        KbdzpRecord record = new KbdzpRecord();
                        record.setAccountId(id);
                        kbdzpRepository.save(record);
                    }
                });
        kbdzpRepository.flush();
    }

    @RegisterScheduledTask(cronExpression = "0 0 12 ? * SAT", executeIfNew = true)
    public void regenerateBoosterActivationCodes() {
        String code1 = codeGenerator.generateCode();
        String code2 = codeGenerator.generateCode();
        KbdzpSharedRecord kbdzpSharedRecord = kbdzpSharedRepository.getTheRecordForWrite();
        kbdzpSharedRecord.setBooster1ActivationCode(code1);
        kbdzpSharedRecord.setBooster2ActivationCode(code2);
        LOG.info("块币大转盘能量回复奖励激活码已更新为 {} 和 {}", code1, code2);
    }

    @EventListener
    public void onAccountCreated(AccountCreatedEvent event) {
        KbdzpRecord record = new KbdzpRecord(new Date(timeProvider.currentTime()));
        record.setAccountId(event.getAccount().getId());
        kbdzpRepository.saveAndFlush(record);
    }

    @EventListener
    public void onInviterRecordCreated(InviterRecordCreatedEvent event) {
        KbdzpRecord record = kbdzpRepository.findByIdForWrite(event.getAccountId());
        if (event.isInvited()) {
            record.setInviteeBonusAvailable(true);
        }
    }

    @Override
    public void hourlyReset() {
        KbdzpSharedRecord kbdzpSharedRecord = kbdzpSharedRepository.getTheRecordForWrite();
        long publicPool = kbdzpSharedRecord.getPublicPool();
        long increaseMilliKuaibi = Math.min(KbdzpConstants.PUBLIC_POOL_RECOVER_UPPER_LIMIT, publicPool + KbdzpConstants.PUBLIC_POOL_RECOVER_PER_HOUR) - publicPool;
        publicPool += increaseMilliKuaibi;
        kbdzpSharedRecord.setPublicPool(publicPool);
        LOG.info("块币大转盘池已增加，当前为：{}", publicPool);
    }

    @Override
    public void dailyReset() {
        List<KbdzpRecord> records = kbdzpRepository.findAll();
        for (KbdzpRecord record : records) {
            record.setTodayTurnCount(0);
            currencyService.decreaseCurrency(record.getAccountId(),
                    CurrencyConstants.ID_能量,
                    currencyService.findOrCreateRecord(record.getAccountId(), CurrencyConstants.ID_能量).getAmount());
        }
        kbdzpRepository.saveAll(records);
    }

    @RegisterScheduledTask(cronExpression = "0 1 0 ? * SAT", executeIfNew = true)
    public void open() {
        fever = true;
    }

    @RegisterScheduledTask(cronExpression = "0 59 23 ? * SUN", executeIfNew = true)
    public void close() {
        fever = false;
    }

    public boolean isFever() {
        return fever;
    }

}
