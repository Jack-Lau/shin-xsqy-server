/*
 * Created 2018-9-1 17:12:46
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class YibenwanliService {

    private static final Logger LOG = LoggerFactory.getLogger(YibenwanliService.class);

    @Autowired
    private YibenwanliSharedRepository yibenwanliSharedRepository;
    @Autowired
    private YibenwanliRepository yibenwanliRepository;
    @Autowired
    private YibenwanliConclusionLogRepository yibenwanliConclusionLogRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private CompositePlayerService compositePlayerService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private long lastReachedPoolGoal = 0;

    public void init() {
        yibenwanliSharedRepository.init(new YibenwanliSharedRecord());

        YibenwanliSharedRecord record = yibenwanliSharedRepository.getTheRecordForWrite();

        // 补回停机的时间
        if (!record.isClosed() && record.getDeadlineTime() != null) {
            Instant currentInstant = timeProvider.currentInstant();
            if (record.getPausedTime() != null) {
                Duration pausedDuration = Duration.between(record.getPausedTime().toInstant(), currentInstant);
                record.pushBackDeadline(currentInstant, pausedDuration);
                record.setPausedTime(null);
            }
            if (Duration.between(currentInstant, record.getDeadlineTime().toInstant()).compareTo(YibenwanliConstants.DURATION_TO_DEADLINE_LIMIT_PAUSE_COMPENSATION) < 0) {
                record.pushBackDeadline(currentInstant, YibenwanliConstants.DURATION_PAUSE_COMPENSATION);
            }
        }

        lastReachedPoolGoal = record.getPool();
    }

    public void destroy() {
        YibenwanliSharedRecord record = yibenwanliSharedRepository.getTheRecordForWrite();
        record.setPausedTime(new Date(timeProvider.currentTime()));
    }

    public YibenwanliRecord purchaseTicket(long accountId, long expectedPrice) {
        Instant currentInstant = timeProvider.currentInstant();
        YibenwanliRecord record = findOrCreatePlayerRecord(accountId);
        checkPlayerLevel(accountId);
        // 检查购买时间间隔
        if (record.getLastPurchaseTime() != null && Duration.between(record.getLastPurchaseTime().toInstant(), currentInstant).compareTo(YibenwanliConstants.DURATION_PURCHASE_INTERVAL) < 0) {
            throw YibenwanliException.purchaseTooFast();
        }
        YibenwanliSharedRecord sharedRecord = yibenwanliSharedRepository.getTheRecordForWrite();
        // 检查本期活动是否已结束
        if (sharedRecord.isClosed() || sharedRecord.getDeadlineTime() != null && currentInstant.toEpochMilli() > sharedRecord.getDeadlineTime().getTime()) {
            throw YibenwanliException.timeup();
        }
        // 检查期望的价格是否一致
        long currentPrice = sharedRecord.getTicketPrice();
        if (expectedPrice != currentPrice) {
            throw YibenwanliException.unmatchedPrice();
        }
        // 检查货币是否足够
        long ybToUse = currentPrice;
        CurrencyRecord ybRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_元宝);
        if (ybRecord.getAmount() < ybToUse) {
            throw YibenwanliException.insufficientCurrency();
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, ybToUse, true, CurrencyConstants.PURPOSE_DECREMENT_一本万利购买本票);

        sharedRecord.addToPool((long) (ybToUse * YibenwanliConstants.RATE_POOL_CONVERSION));
        sharedRecord.increaseTotalTicketCount();
        sharedRecord.setLastPurchaseAccountId(accountId);
        //
        record.increaseTicketCount();
        record.setLastPurchaseTime(Date.from(currentInstant));
        record = yibenwanliRepository.save(record);
        //
        LongStream.of(YibenwanliConstants.POOL_GOALS)
                .filter(it -> it > lastReachedPoolGoal && it <= sharedRecord.getPool())
                .findAny().ifPresent(it -> {
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    YibenwanliConstants.BROADCAST_ID_REACH_GOAL,
                                    ImmutableMap.of("jackpot", sharedRecord.getPool())
                            )
                    );
                    lastReachedPoolGoal = sharedRecord.getPool();
                });

        eventPublisher.publishEvent(new YibenwanliBuyEvent(this, record));
        //
        if (sharedRecord.getDeadlineTime() == null) {
            sharedRecord.setDeadlineTime(Date.from(currentInstant.plus(YibenwanliConstants.DURATION_MAX_TO_DEADLINE)));
        } else {
            if (RandomProvider.getRandom().nextDouble() < sharedRecord.getLastShotRate(currentInstant)) {
                sharedRecord.setDeadlineTime(new Date());
                yibenwanliSharedRepository.save(sharedRecord);
                checkForConclusion();
            } else {
                sharedRecord.pushBackDeadline(currentInstant, YibenwanliConstants.DURATION_PUSH_BACK_PER_PURCHASE);
            }
        }
        //
        return record;
    }

    public YibenwanliRecord findOrCreatePlayerRecord(long accountId) {
        YibenwanliRecord record = yibenwanliRepository.findByIdForWrite(accountId);
        if (record == null) {
            record = new YibenwanliRecord();
            record.setAccountId(accountId);
            record = yibenwanliRepository.save(record);
        }
        return record;
    }

    public void checkForStart() {
        YibenwanliSharedRecord sharedRecord = yibenwanliSharedRepository.getTheRecordForWrite();
        // 如果 closed 为 true 而 nextSessionTime 为空则说明是初次启动
        if (sharedRecord.isClosed() && (sharedRecord.getNextSeasonTime() == null || sharedRecord.getNextSeasonTime().getTime() <= timeProvider.currentTime())) {
            long initPoolYb = YibenwanliConstants.POOL_MIN_VALUE;
            sharedRecord.setPool(initPoolYb);
            sharedRecord.setTotalTicketCount(0);
            sharedRecord.setLastPurchaseAccountId(null);
            sharedRecord.setDeadlineTime(null);
            sharedRecord.setPausedTime(null);
            sharedRecord.setClosed(false);
            sharedRecord.setNextSeasonTime(null);

            lastReachedPoolGoal = 0;

            yibenwanliRepository.deleteAll();

            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(YibenwanliConstants.BROADCAST_ID_START, Collections.emptyMap())
            );
        }
    }

    public void checkForConclusion() {
        Instant currentInstant = timeProvider.currentInstant();
        YibenwanliSharedRecord sharedRecord = yibenwanliSharedRepository.getTheRecordForWrite();
        if (!sharedRecord.isClosed() && sharedRecord.getDeadlineTime() != null && sharedRecord.getDeadlineTime().getTime() <= currentInstant.toEpochMilli()) {
            long finalPool = sharedRecord.getPool();
            long awardForLastOne = (long) (finalPool * YibenwanliConstants.RATE_AWARD_FOR_LAST_ONE);
            int divisor = Math.max(sharedRecord.getTotalTicketCount(), YibenwanliConstants.DIVISOR_MIN);
            long awardForPublicEach = (long) (finalPool * YibenwanliConstants.RATE_AWARD_FOR_PUBLIC) / divisor;
            long awardForPublicTotal = awardForPublicEach * sharedRecord.getTotalTicketCount();
            long awardForLuckyOne = (long) (finalPool * YibenwanliConstants.RATE_AWARD_FOR_LUCK);

            MailSendingRequest.create()
                    .template(YibenwanliConstants.MAIL_ID_LAST_ONE_AWARD)
                    .attachment(Collections.singletonList(new CurrencyStack(CurrencyConstants.ID_元宝, awardForLastOne)))
                    .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_一本万利奖金)
                    .to(sharedRecord.getLastPurchaseAccountId())
                    .commit(mailService);

            List<YibenwanliRecord> allRecords = yibenwanliRepository.findAll();
            int actualTotalTicketCount = allRecords.stream()
                    .mapToInt(YibenwanliRecord::getTicketCount)
                    .sum();
            int luckyNumber = RandomProvider.getRandom().nextInt(actualTotalTicketCount);
            long luckyOneAccountId = -1;
            boolean luckyOneDecided = false;
            for (YibenwanliRecord record : allRecords) {
                MailSendingRequest.create()
                        .template(YibenwanliConstants.MAIL_ID_PUBLIC_AWARD)
                        .attachment(Collections.singletonList(new CurrencyStack(CurrencyConstants.ID_元宝, awardForPublicEach * record.getTicketCount())))
                        .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_一本万利奖金)
                        .to(record.getAccountId())
                        .commit(mailService);
                if (!luckyOneDecided) {
                    luckyNumber -= record.getTicketCount();
                    if (luckyNumber < 0) {
                        luckyOneDecided = true;
                        luckyOneAccountId = record.getAccountId();
                        MailSendingRequest.create()
                                .template(YibenwanliConstants.MAIL_ID_LUCKY_ONE_AWARD)
                                .attachment(Collections.singletonList(new CurrencyStack(CurrencyConstants.ID_元宝, awardForLuckyOne)))
                                .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_一本万利奖金)
                                .to(record.getAccountId())
                                .commit(mailService);
                    }
                }
            }

            String lastPurchasePlayerName = Optional.ofNullable(sharedRecord.getLastPurchaseAccountId())
                    .map(id -> playerRepository.findById(id).orElse(null))
                    .map(Player::getPlayerName)
                    .orElse("用户名不存在");
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            YibenwanliConstants.BROADCAST_ID_CONCLUSION,
                            ImmutableMap.of("playerName", lastPurchasePlayerName, "topPrize", awardForLastOne)
                    )
            );

            String luckyOnePlayerName = Optional.ofNullable(luckyOneAccountId)
                    .map(id -> playerRepository.findById(id).orElse(null))
                    .map(Player::getPlayerName)
                    .orElse("用户名不存在");
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            YibenwanliConstants.BROADCAST_ID_LUCKY_ONE,
                            ImmutableMap.of("playerName", luckyOnePlayerName, "luckyPrize", awardForLuckyOne)
                    )
            );

            sharedRecord.setNextSeasonInitPool(0);
            sharedRecord.setClosed(true);
            sharedRecord.setNextSeasonTime(Date.from(currentInstant.plus(YibenwanliConstants.DURATION_TO_NEXT_SEASON)));

            YibenwanliConclusionLog log = new YibenwanliConclusionLog();
            log.setFinalPool(finalPool);
            log.setTotalTicketCount(sharedRecord.getTotalTicketCount());
            log.setAwardForLastOne(awardForLastOne);
            log.setAwardPerTicket(awardForPublicEach);
            log.setLastPurchaseAccountId(sharedRecord.getLastPurchaseAccountId());
            log.setEventTime(new Date(timeProvider.currentTime()));
            log.setAwardForLuckyOne(awardForLuckyOne);
            log.setLuckyOneAccountId(luckyOneAccountId);

            yibenwanliConclusionLogRepository.save(log);
        }
    }

    @Transactional(readOnly = true)
    public void tryPublishLastChangeBroadcast() {
        YibenwanliSharedRecord sharedRecord = yibenwanliSharedRepository.getTheRecord();
        if (Duration.between(timeProvider.currentInstant(), sharedRecord.getDeadlineTime().toInstant()).compareTo(YibenwanliConstants.DURATION_LAST_CHANCE) <= 0) {
            String lastPurchasePlayerName = Optional.ofNullable(sharedRecord.getLastPurchaseAccountId())
                    .map(id -> playerRepository.findById(id).orElse(null))
                    .map(Player::getPlayerName)
                    .orElse("用户名不存在");
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            YibenwanliConstants.BROADCAST_ID_LAST_CHANCE,
                            ImmutableMap.of("playerName", lastPurchasePlayerName)
                    )
            );
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < YibenwanliConstants.PLAYER_LEVEL_REQUIRE) {
            throw YibenwanliException.等级不足();
        }
    }

}
