/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.game.secretShop.SecretShopRecord.Prize;
import cn.com.yting.kxy.web.game.secretShop.resource.SecretShopJackpot;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.price.PriceService;
import cn.com.yting.kxy.web.quest.QuestRecord;
import cn.com.yting.kxy.web.quest.QuestRepository;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class SecretShopService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    PriceService priceService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;

    @Autowired
    SecretShopRepository secretShopRepository;
    @Autowired
    SecretShopSharedRepository secretShopSharedRepository;
    @Autowired
    SecretShopPrizeGrantingStatsRepository secretShopPrizeGrantingStatsRepository;
    @Autowired
    QuestRepository questRepository;

    @Autowired
    ResourceContext resourceContext;

    RandomSelector<Long> jackpot;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (secretShopSharedRepository.count() <= 0) {
            SecretShopSharedRecord secretShopSharedRecord = new SecretShopSharedRecord();
            secretShopSharedRecord.setKcPackPrice(SecretShopConstants.KC_PACK_LOWER_PRICE);
            secretShopSharedRecord.setKcPackRemainCount(SecretShopConstants.KC_PACK_MAX_CAPACITY);
            secretShopSharedRepository.init(secretShopSharedRecord);
        }
        //
        RandomSelectorBuilder jackpotBuilder = RandomSelector.<Long>builder();
        List<SecretShopJackpot> jackpots = new ArrayList<>(resourceContext.getLoader(SecretShopJackpot.class).getAll().values());
        jackpots.forEach((ssj) -> {
            if (ssj.getSerialNumber() != 0) {
                jackpotBuilder.add(ssj.getId(), ssj.getProbability());
            }
        });
        jackpot = jackpotBuilder.build(RandomSelectType.DEPENDENT);
    }

    public SecretShopOverall get(long accountId) {
        return new SecretShopOverall(secretShopSharedRepository.getTheRecord(), findOrCreate(accountId), price(accountId));
    }

    public List<SecretShopPrizeGrantingStats> getGrantingStats() {
        return secretShopPrizeGrantingStatsRepository.findAll();
    }

    public List<Long> price(long accountId) {
        List<Long> prices = new ArrayList<>();
        prices.add(secretShopSharedRepository.getTheRecord().getKcPackPrice());
        prices.add(priceService.getCurrentPrice(SecretShopConstants.PRIZE_DRAW_FLOATING_PRICE_MODEL));
        return prices;
    }

    public SecretShopOverall draw(long accountId, long expectedPrice, boolean batchDraw) {
        SecretShopRecord secretShopRecord = findOrCreate(accountId);
        if (secretShopRecord.getNotTakePrizes().size() > 0) {
            throw SecretShopException.haveNotTakenPrize();
        }
        checkLevelAndQuest(accountId);
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc() < SecretShopConstants.PLAYER_FC_REQUIRE) {
            throw SecretShopException.insufficientPlayerFc();
        }
        int drawCount = batchDraw ? SecretShopConstants.BATCH_DRAW_COUNT : 1;
        priceService.deduct(accountId,
                SecretShopConstants.PRIZE_DRAW_FLOATING_PRICE_MODEL,
                expectedPrice,
                CurrencyConstants.PURPOSE_DECREMENT_神秘商店抽奖,
                drawCount,
                drawCount);
        //
        List<Prize> prizes = new ArrayList<>();
        for (int i = 0; i < drawCount; i++) {
            while (true) {
                long jackpotId = jackpot.getSingle();
                SecretShopJackpot ssj = resourceContext.getLoader(SecretShopJackpot.class).get(jackpotId);
                if (ssj.getLimit() == 0) {
                    prizes.add(new Prize(ssj.getId(), ssj.getCurrencyId(), ssj.getCurrencyAmount()));
                    break;
                } else {
                    SecretShopPrizeGrantingStats sspgs = secretShopPrizeGrantingStatsRepository.findOrCreateById(ssj.getId());
                    if (sspgs.getGrantedCount() < ssj.getLimit()) {
                        sspgs.increaseGrantedCount();
                        secretShopPrizeGrantingStatsRepository.save(sspgs);
                        prizes.add(new Prize(ssj.getId(), ssj.getCurrencyId(), ssj.getCurrencyAmount()));
                        break;
                    }
                }
            }
        }
        secretShopRecord.setNotTakePrizes(prizes);
        secretShopRecord = secretShopRepository.save(secretShopRecord);
        return new SecretShopOverall(secretShopSharedRepository.getTheRecord(), secretShopRecord, price(accountId));
    }

    public SecretShopOverall take(long accountId) {
        SecretShopRecord secretShopRecord = findOrCreate(accountId);
        if (secretShopRecord.getNotTakePrizes().size() < 1) {
            throw SecretShopException.notHaveNotTakenPrize();
        }
        List<Prize> takenPrizes = new ArrayList<>(secretShopRecord.getNotTakePrizes());
        secretShopRecord.setNotTakePrizes(new ArrayList<>());
        secretShopRecord = secretShopRepository.save(secretShopRecord);
        //
        takenPrizes.forEach((p) -> {
            currencyService.increaseCurrency(accountId, p.getCurrencyId(), p.getCurrencyAmount(), CurrencyConstants.PURPOSE_INCREMENT_神秘商店抽奖);
            //
            SecretShopJackpot ssj = resourceContext.getLoader(SecretShopJackpot.class).get(p.getJackpotId());
            if (ssj.getBroadcastId() != 0) {
                chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                        ssj.getBroadcastId(),
                        ImmutableMap.of(
                                "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                "amount", p.getCurrencyId() == CurrencyConstants.ID_毫仙石 ? p.getCurrencyAmount() / 1000 : p.getCurrencyAmount(),
                                "currency", Currency.getFrom(resourceContext, p.getCurrencyId()).getName())
                ));
            }
        });
        return new SecretShopOverall(secretShopSharedRepository.getTheRecord(), secretShopRecord, price(accountId), takenPrizes);
    }

    public SecretShopOverall exchange(long accountId) {
        checkLevelAndQuest(accountId);
        SecretShopSharedRecord secretShopSharedRecord = secretShopSharedRepository.getTheRecord();
        if (secretShopSharedRecord.getKcPackRemainCount() < 1) {
            throw SecretShopException.insufficientKCPack();
        }
        //
        SecretShopRecord secretShopRecord = findOrCreate(accountId);
        if (secretShopRecord.getKcPackExchangeCount() >= SecretShopConstants.KC_PACK_MAX_EXCHANGE_COUNT) {
            throw SecretShopException.kcPackExchangeCountReachLimit();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_经验, secretShopSharedRecord.getKcPackPrice(), true, CurrencyConstants.PURPOSE_DECREMENT_神秘商店兑换);
        secretShopSharedRecord.setKcPackRemainCount(secretShopSharedRecord.getKcPackRemainCount() - 1);
        secretShopSharedRecord = secretShopSharedRepository.save(secretShopSharedRecord);
        secretShopRecord.setKcPackExchangeCount(secretShopRecord.getKcPackExchangeCount() + 1);
        secretShopRecord = secretShopRepository.save(secretShopRecord);
        //
        currencyService.increaseCurrency(accountId, SecretShopConstants.KC_PACK_CURRENCY_ID, 1, CurrencyConstants.PURPOSE_INCREMENT_神秘商店兑换);
        return new SecretShopOverall(secretShopSharedRecord, findOrCreate(accountId), price(accountId));
    }

    @Override
    public void dailyReset() {
        SecretShopSharedRecord secretShopSharedRecord = secretShopSharedRepository.getTheRecord();
        secretShopSharedRecord.setKcPackRemainCount(SecretShopConstants.KC_PACK_MAX_CAPACITY);
        secretShopSharedRepository.save(secretShopSharedRecord);
    }

    private SecretShopRecord findOrCreate(long accountId) {
        SecretShopRecord secretShopRecord = secretShopRepository.findByAccountId(accountId);
        if (secretShopRecord == null) {
            secretShopRecord = new SecretShopRecord();
            secretShopRecord.setAccountId(accountId);
            secretShopRecord.setKcPackExchangeCount(0);
            secretShopRecord.setNotTakePrizes(new ArrayList<>());
            secretShopRecord = secretShopRepository.save(secretShopRecord);
        }
        return secretShopRecord;
    }

    private void checkLevelAndQuest(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < SecretShopConstants.PLAYER_LEVEL_REQUIRE) {
            throw SecretShopException.insufficientPlayerLevel();
        }
        QuestRecord questRecord = questRepository.findById(accountId, SecretShopConstants.PREREQUIREMENT_QUEST_ID).orElse(null);
        if (questRecord == null || !questRecord.getQuestStatus().equals(QuestStatus.COMPLETED)) {
            throw SecretShopException.questNotFinish();
        }
    }

}
