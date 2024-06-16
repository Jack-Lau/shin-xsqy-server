/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.award.model.Award;
import cn.com.yting.kxy.web.award.resource.Awards;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.game.antique.resource.WesternMerchant;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class AntiqueService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;
    @Autowired
    AwardService awardService;
    @Autowired
    MailService mailService;

    @Autowired
    AntiqueRepository antiqueRepository;
    @Autowired
    AntiqueSharedRepository antiqueSharedRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (antiqueSharedRepository.count() <= 0) {
            antiqueSharedRepository.init(new AntiqueSharedRecord());
        }
    }

    public AntiqueOverall get(long accountId) {
        return new AntiqueOverall(
                antiqueSharedRepository.getTheRecord(),
                findOrCreateAntiqueRecord(accountId),
                null);
    }

    public AntiqueOverall buy(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        checkYbCost(accountId, AntiqueConstants.BUY_YB_COST);
        //
        Optional<AntiqueRecord> antiqueRecord = antiqueRepository.findByAccountIdForWrite(accountId);
        if (antiqueRecord.isPresent()) {
            AntiqueRecord ar = antiqueRecord.get();
            if (ar.isStarted()) {
                throw AntiqueException.alreadyInRepair();
            }
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, AntiqueConstants.BUY_YB_COST, true, CurrencyConstants.PURPOSE_DECREMENT_购买_修复古董);
            ar.setStarted(true);
            ar.setRepairCount(0);
            WesternMerchant westernMerchant = resourceContext.getLoader(WesternMerchant.class).get(ar.getRepairCount());
            ar.setProgress((int) (westernMerchant.getLowerPercent() + (westernMerchant.getUpperPercent() - westernMerchant.getLowerPercent()) * RandomProvider.getRandom().nextDouble()));
            ar.setPart(null);
            ar = antiqueRepository.save(ar);
            eventPublisher.publishEvent(new AntiqueBuyEvent(this, ar));
            return new AntiqueOverall(
                    antiqueSharedRepository.getTheRecord(),
                    ar,
                    null);
        } else {
            throw AntiqueException.notStartedYet();
        }
    }

    public AntiqueOverall sell(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        Optional<AntiqueRecord> antiqueRecord = antiqueRepository.findByAccountIdForWrite(accountId);
        if (antiqueRecord.isPresent()) {
            AntiqueRecord ar = antiqueRecord.get();
            if (!ar.isStarted()) {
                throw AntiqueException.doNotHaveAntique();
            }
            //
            WesternMerchant westernMerchant = resourceContext.getLoader(WesternMerchant.class).get(ar.getRepairCount());
            ar.setStarted(false);
            ar.setRepairCount(0);
            ar.setProgress(0);
            ar.setPart(null);
            ar = antiqueRepository.save(ar);
            AwardResult awardResult = awardService.processAward(accountId, westernMerchant.getSellAward(), CurrencyConstants.PURPOSE_INCREMENT_卖出古董);
            //
            if (westernMerchant.getRefreshServerAward() != 0) {
                AntiqueSharedRecord asr = antiqueSharedRepository.getTheRecordForWrite();
                asr.setPublicAwardAccountId(accountId);
                asr.setPublicAwardRemainCount(AntiqueConstants.REFRESH_SERVER_AWARD_COUNT);
                asr.setLastPublicAwardCreateTime(new Date(timeProvider.currentTime()));
                antiqueSharedRepository.save(asr);
            }
            //
            if (westernMerchant.getSellBroadcast() != 0) {
                long yb = 0;
                for (CurrencyStack cs : awardResult.getCurrencyStacks()) {
                    if (cs.getCurrencyId() == CurrencyConstants.ID_元宝) {
                        yb = cs.getAmount();
                        break;
                    }
                }
                chatService.sendSystemMessage(
                        ChatConstants.SERVICE_ID_UNDIFINED,
                        ChatMessage.createTemplateMessage(
                                westernMerchant.getSellBroadcast(),
                                ImmutableMap.of(
                                        "playerName", compositePlayerService.getPlayerBaseInfo(ar.getAccountId()).getPlayer().getPlayerName(),
                                        "amount", yb
                                )
                        )
                );
            }
            if (westernMerchant.getId() >= AntiqueConstants.UI_BROADCAST_ANTIQUE_LEVEL_REQUIRE) {
                long yb = 0;
                for (CurrencyStack cs : awardResult.getCurrencyStacks()) {
                    if (cs.getCurrencyId() == CurrencyConstants.ID_元宝) {
                        yb = cs.getAmount();
                        break;
                    }
                }
                chatService.offerInterestingMessage(
                        AntiqueConstants.UI_BROADCAST_ID,
                        ChatMessage.createTemplateMessage(
                                AntiqueConstants.UI_BROADCAST_ID,
                                ImmutableMap.of(
                                        "playerName", compositePlayerService.getPlayerBaseInfo(ar.getAccountId()).getPlayer().getPlayerName(),
                                        "amount", yb
                                )
                        )
                );
            }
            //
            return new AntiqueOverall(
                    antiqueSharedRepository.getTheRecord(),
                    ar,
                    awardResult);
        } else {
            throw AntiqueException.notStartedYet();
        }
    }

    public AntiqueOverall repair(long accountId, String part) {
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        Optional<AntiqueRecord> antiqueRecord = antiqueRepository.findByAccountIdForWrite(accountId);
        if (antiqueRecord.isPresent()) {
            AntiqueRecord ar = antiqueRecord.get();
            if (!ar.isStarted()) {
                throw AntiqueException.doNotHaveAntique();
            }
            //
            if (ar.getRepairCount() + 1 >= resourceContext.getLoader(WesternMerchant.class).getAll().values().size()) {
                throw AntiqueException.antiqueLevelMax();
            }
            //
            if (ar.getPart() != null && ar.getPart().equals(part)) {
                throw AntiqueException.修复了同一个部位();
            }
            //
            WesternMerchant westernMerchant = resourceContext.getLoader(WesternMerchant.class).get(ar.getRepairCount());
            checkYbCost(accountId, westernMerchant.getRepairPrice());
            //
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, westernMerchant.getRepairPrice(), true, CurrencyConstants.PURPOSE_DECREMENT_购买_修复古董);
            AwardResult failAward = null;
            if (RandomProvider.getRandom().nextDouble() < westernMerchant.getSuccessRate()) {
                ar.setRepairCount(ar.getRepairCount() + 1);
                westernMerchant = resourceContext.getLoader(WesternMerchant.class).get(ar.getRepairCount());
                ar.setProgress((int) (westernMerchant.getLowerPercent() + (westernMerchant.getUpperPercent() - westernMerchant.getLowerPercent()) * RandomProvider.getRandom().nextDouble()));
                ar.setPart(part);
            } else {
                ar.setStarted(false);
                ar.setRepairCount(0);
                ar.setProgress(0);
                ar.setPart(null);
                if (westernMerchant.getFailAward() != 0) {
                    failAward = awardService.processAward(accountId, westernMerchant.getFailAward(), CurrencyConstants.PURPOSE_DECREMENT_购买_修复古董);
                }
            }
            ar = antiqueRepository.save(ar);
            //
            return new AntiqueOverall(
                    antiqueSharedRepository.getTheRecord(),
                    ar,
                    failAward);
        } else {
            throw AntiqueException.notStartedYet();
        }
    }

    public AntiqueOverall take(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        Optional<AntiqueRecord> antiqueRecord = antiqueRepository.findByAccountIdForWrite(accountId);
        if (antiqueRecord.isPresent()) {
            AntiqueRecord ar = antiqueRecord.get();
            if (ar.getPublicAwardObtainCount() >= AntiqueConstants.MAX_PUBLIC_AWARD_OBTAIN_COUNT) {
                throw AntiqueException.awardTakeCountMax();
            }
            AntiqueSharedRecord asr = antiqueSharedRepository.getTheRecordForWrite();
            if (asr.getPublicAwardRemainCount() <= 0) {
                throw AntiqueException.insufficientAwardRemain();
            }
            if (ar.getLastPublicAwardObtainTime().after(asr.getLastPublicAwardCreateTime())) {
                throw AntiqueException.awardTaken();
            }
            asr.setPublicAwardRemainCount(asr.getPublicAwardRemainCount() - 1);
            asr = antiqueSharedRepository.save(asr);
            ar.setPublicAwardObtainCount(ar.getPublicAwardObtainCount() + 1);
            ar.setLastPublicAwardObtainTime(new Date(timeProvider.currentTime()));
            ar = antiqueRepository.save(ar);
            AwardResult awardResult = awardService.processAward(accountId, AntiqueConstants.SERVER_AWARD_ID, CurrencyConstants.PURPOSE_INCREMENT_古董全服奖励);
            //
            for (CurrencyStack cs : awardResult.getCurrencyStacks()) {
                if (cs.getCurrencyId() == CurrencyConstants.ID_元宝
                        && cs.getAmount() > 0) {
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    AntiqueConstants.TAKE_BROADCAST_ID,
                                    ImmutableMap.of(
                                            "playerName", compositePlayerService.getPlayerBaseInfo(ar.getAccountId()).getPlayer().getPlayerName()
                                    )
                            )
                    );
                    break;
                }
            }
            //
            return new AntiqueOverall(
                    asr,
                    ar,
                    awardResult);
        } else {
            throw AntiqueException.notStartedYet();
        }
    }

    public void end() {
        List<AntiqueRecord> antiqueRecords = antiqueRepository.findAll();
        List<AntiqueRecord> updatedRecords = new ArrayList<>();
        Date currentDate = new Date(timeProvider.currentTime());
        for (AntiqueRecord ar : antiqueRecords) {
            if (ar.isStarted()) {
                WesternMerchant westernMerchant = resourceContext.getLoader(WesternMerchant.class).get(ar.getRepairCount());
                Awards awards = Awards.getFrom(resourceContext, westernMerchant.getSellAward());
                long playerFc = playerRepository.findById(ar.getAccountId()).map(Player::getFc).orElse(0L);
                Award award = awards.createAward(compositePlayerService.getPlayerBaseInfo(ar.getAccountId()).getPlayer().getPlayerLevel(), playerFc);
                List<CurrencyStack> cs = new ArrayList<>();
                award.getCurrencyChanceMap().keySet().forEach((currencyId) -> {
                    if (award.getCurrencyChanceMap().get(currencyId) != 0) {
                        cs.add(new CurrencyStack(currencyId, award.getCurrencyChanceMap().get(currencyId)));
                    }
                });
                MailSendingRequest.create()
                        .template(AntiqueConstants.MAIL_END_ID)
                        .attachment(cs)
                        .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_卖出古董)
                        .to(ar.getAccountId())
                        .commit(mailService);
            }
            ar.setStarted(false);
            ar.setRepairCount(0);
            ar.setProgress(0);
            ar.setPart(null);
            ar.setPublicAwardObtainCount(0);
            ar.setLastPublicAwardObtainTime(currentDate);
            updatedRecords.add(ar);
        }
        antiqueRepository.saveAll(updatedRecords);
        //
        AntiqueSharedRecord asr = antiqueSharedRepository.getTheRecordForWrite();
        asr.setPublicAwardRemainCount(0);
        asr.setPublicAwardAccountId(null);
        antiqueSharedRepository.save(asr);
    }

    @Override
    public void dailyReset() {
        List<AntiqueRecord> antiqueRecords = antiqueRepository.findAll();
        List<AntiqueRecord> updatedRecords = new ArrayList<>();
        for (AntiqueRecord ar : antiqueRecords) {
            ar.setPublicAwardObtainCount(0);
            updatedRecords.add(ar);
        }
        antiqueRepository.saveAll(updatedRecords);
    }

    private AntiqueRecord findOrCreateAntiqueRecord(long accountId) {
        AntiqueRecord antiqueRecord = antiqueRepository.findByAccountId(accountId);
        if (antiqueRecord == null) {
            antiqueRecord = new AntiqueRecord();
            antiqueRecord.setAccountId(accountId);
            antiqueRecord.setStarted(false);
            antiqueRecord.setRepairCount(0);
            antiqueRecord.setProgress(0);
            antiqueRecord.setPart(null);
            antiqueRecord.setLastPublicAwardObtainTime(new Date(timeProvider.currentTime()));
            antiqueRecord.setPublicAwardObtainCount(0);
            antiqueRecord = antiqueRepository.save(antiqueRecord);
        }
        return antiqueRecord;
    }

    private void checkAvailable() {
        if (!AntiqueConstants.AVAILABLE) {
            throw AntiqueException.notStartedYet();
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < AntiqueConstants.PLAYER_LEVEL_REQUIRE) {
            throw AntiqueException.insufficientPlayerLevel();
        }
    }

    private void checkYbCost(long accountId, long amount) {
        if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_元宝).getAmount() < amount) {
            throw AntiqueException.insufficientCurrency();
        }
    }

}
