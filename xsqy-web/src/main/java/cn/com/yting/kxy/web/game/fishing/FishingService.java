/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
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
import cn.com.yting.kxy.web.game.fishing.resource.FishCategory;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
@Transactional
public class FishingService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;

    @Autowired
    FishingRepository fishingRepository;
    @Autowired
    FishingOnceRepository fishingOnceRepository;

    @Autowired
    ResourceContext resourceContext;
    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    RandomSelector<Long> normalFishJackpot;
    RandomSelector<Long> rareFishJackpot;

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder normalFishJackpotBuilder = RandomSelector.<Long>builder();
        RandomSelectorBuilder rareFishJackpotBuilder = RandomSelector.<Long>builder();
        List<FishCategory> fishCategory = new ArrayList<>(resourceContext.getLoader(FishCategory.class).getAll().values());
        fishCategory.forEach((fc) -> {
            if (fc.getRare() != 0) {
                rareFishJackpotBuilder.add(fc.getId(), fc.getProbability());
            } else {
                normalFishJackpotBuilder.add(fc.getId(), fc.getProbability());
            }
        });
        normalFishJackpot = normalFishJackpotBuilder.build(RandomSelectType.DEPENDENT);
        rareFishJackpot = rareFishJackpotBuilder.build(RandomSelectType.DEPENDENT);
    }

    public FishingOverall get(long accountId) {
        checkLevelAndFc(accountId);
        return new FishingOverall(findOrCreate(accountId), fishingOnceRepository.findByAccountId(accountId));
    }

    public FishingOverall buy(long accountId) {
        checkLevelAndFc(accountId);
        FishingRecord fr = findOrCreate(accountId);
        boolean buyNormal = fr.getTodayBuyFishingPoleCount() >= FishingConstants.MAX_BUY_RARE_POLE_PER_DAY;
        if (buyNormal) {
            if (currencyService.getCurrencyAmount(accountId, FishingConstants.BUY_NORMAL_POLE_CURRENCY_ID) < FishingConstants.BUY_NORMAL_POLE_CURRENCY_AMOUNT) {
                throw FishingException.insufficientGold();
            }
        } else {
            if (currencyService.getCurrencyAmount(accountId, FishingConstants.BUY_RARE_POLE_CURRENCY_ID) < FishingConstants.BUY_RARE_POLE_CURRENCY_AMOUNT) {
                throw FishingException.insufficientGold();
            }
        }
        //
        currencyService.decreaseCurrency(accountId,
                buyNormal ? FishingConstants.BUY_NORMAL_POLE_CURRENCY_ID : FishingConstants.BUY_RARE_POLE_CURRENCY_ID,
                buyNormal ? FishingConstants.BUY_NORMAL_POLE_CURRENCY_AMOUNT : FishingConstants.BUY_RARE_POLE_CURRENCY_AMOUNT,
                true, CurrencyConstants.PURPOSE_DECREMENT_购买钓竿);
        currencyService.increaseCurrency(accountId,
                buyNormal ? FishingConstants.NORMAL_POLE_CURRENCY_ID : FishingConstants.RARE_POLE_CURRENCY_ID,
                1,
                CurrencyConstants.PURPOSE_INCREMENT_购买钓竿);
        //
        fr.setTodayBuyFishingPoleCount(fr.getTodayBuyFishingPoleCount() + 1);
        fishingRepository.save(fr);
        return get(accountId);
    }

    public FishingOverall fish(long accountId) {
        checkLevelAndFc(accountId);
        checkPole(accountId);
        List<FishingOnceRecord> fishingOnceRecords = fishingOnceRepository.findByAccountId(accountId);
        for (FishingOnceRecord fr : fishingOnceRecords) {
            if (!fr.isFinish()) {
                throw FishingException.fishingNotFinish();
            }
        }
        //
        Instant currentInstant = timeProvider.currentInstant();
        FishingRecord fr = findOrCreate(accountId);
        boolean useNormalPole = currencyService.getCurrencyAmount(accountId, FishingConstants.NORMAL_POLE_CURRENCY_ID) > 0;
        boolean getRare = false;
        if (RandomProvider.getRandom().nextDouble() < (useNormalPole ? FishingConstants.USE_NORMAL_POLE_GET_RARE : FishingConstants.USE_RARE_POLE_GET_RARE)) {
            getRare = true;
        }
        long fishCategoryId = getRare ? rareFishJackpot.getSingle() : normalFishJackpot.getSingle();
        FishCategory fc = resourceContext.getLoader(FishCategory.class).get(fishCategoryId);
        FishingOnceRecord fishingOnceRecord = new FishingOnceRecord();
        fishingOnceRecord.setAccountId(accountId);
        fishingOnceRecord.setFishCategoryId(fishCategoryId);
        fishingOnceRecord.setGram(1 + RandomProvider.getRandom().nextInt(FishingConstants.MAX_FISH_GRAM));
        fishingOnceRecord.setAwardCurrencyId(getRare ? CurrencyConstants.ID_元宝 : CurrencyConstants.ID_钓鱼点);
        fishingOnceRecord.setAwardCurrencyAmount((long) (fc.getBase() * (double) fishingOnceRecord.getGram() / 10));
        fishingOnceRecord.setFinish(false);
        fishingOnceRecord.setDuration(10 + RandomProvider.getRandom().nextInt(20));
        fishingOnceRecord.setFinishLimitTime(Date.from(currentInstant.plusSeconds(fishingOnceRecord.getDuration())));
        fishingOnceRepository.save(fishingOnceRecord);
        //
        return get(accountId);
    }

    public FishingOverall finish(long accountId, long fishingOnceRecordId) {
        checkLevelAndFc(accountId);
        checkPole(accountId);
        FishingRecord fr = findOrCreate(accountId);
        FishingOnceRecord fishingOnceRecord = fishingOnceRepository.findById(fishingOnceRecordId).orElse(null);
        if (fishingOnceRecord == null || fishingOnceRecord.getAccountId() != accountId
                || fishingOnceRecord.isFinish() || new Date().before(fishingOnceRecord.getFinishLimitTime())) {
            throw FishingException.cannotFinishFishing();
        }
        //
        fishingOnceRecord.setFinish(true);
        fishingOnceRepository.save(fishingOnceRecord);
        currencyService.increaseCurrency(accountId,
                fishingOnceRecord.getAwardCurrencyId(),
                fishingOnceRecord.getAwardCurrencyAmount(),
                CurrencyConstants.PURPOSE_INCREMENT_钓鱼);
        fr.setTotalFishingCount(fr.getTotalFishingCount() + 1);
        fishingRepository.save(fr);
        if (fr.getTotalFishingCount() % 10 == 0) {
            boolean useNormalPole = currencyService.getCurrencyAmount(accountId, FishingConstants.NORMAL_POLE_CURRENCY_ID) > 0;
            currencyService.decreaseCurrency(accountId,
                    useNormalPole ? FishingConstants.NORMAL_POLE_CURRENCY_ID : FishingConstants.RARE_POLE_CURRENCY_ID,
                    1,
                    true, CurrencyConstants.PURPOSE_DECREMENT_钓鱼);
        }
        eventPublisher.publishEvent(new FishingFinishEvent(this, fr));
        FishCategory fc = resourceContext.getLoader(FishCategory.class).get(fishingOnceRecord.getFishCategoryId());
        if (fc.getColor() == 5 || fc.getRare() != 0) {
            boolean isRare = fc.getRare() != 0;
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    isRare ? FishingConstants.FINISH_RARE_FISH_BROADCAST : FishingConstants.FINISH_ORANGE_FISH_BROADCAST,
                    ImmutableMap.of(
                            "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                            "gram", fishingOnceRecord.getGram(),
                            "fishName", fc.getName(),
                            "currencyAmount", isRare ? fishingOnceRecord.getAwardCurrencyAmount() / 1000 : fishingOnceRecord.getAwardCurrencyAmount())
            ));
        }
        //
        return get(accountId);
    }

    @Override
    public void dailyReset() {
        List<FishingRecord> fishingRecords = fishingRepository.findAll();
        for (FishingRecord fr : fishingRecords) {
            fr.setTodayBuyFishingPoleCount(0);
        }
        fishingRepository.saveAll(fishingRecords);
        //
        List<FishingOnceRecord> fishingOnceRecords = fishingOnceRepository.findAll();
        List<FishingOnceRecord> deleteRecords = new ArrayList<>();
        for (FishingOnceRecord fr : fishingOnceRecords) {
            if (fr.isFinish()) {
                deleteRecords.add(fr);
            }
        }
        fishingOnceRepository.deleteAll(deleteRecords);
    }

    private FishingRecord findOrCreate(long accountId) {
        FishingRecord fr = fishingRepository.findByAccountId(accountId);
        if (fr == null) {
            fr = new FishingRecord();
            fr.setAccountId(accountId);
            fr.setTotalFishingCount(0);
            fr.setTodayBuyFishingPoleCount(0);
            fr = fishingRepository.save(fr);
        }
        return fr;
    }

    private void checkLevelAndFc(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < FishingConstants.PLAYER_LEVEL_REQUIRE) {
            throw FishingException.insufficientPlayerLevel();
        }
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc() < FishingConstants.PLAYER_FC_REQUIRE) {
            throw FishingException.insufficientPlayerFc();
        }
    }

    private void checkPole(long accountId) {
        if (currencyService.getCurrencyAmount(accountId, FishingConstants.NORMAL_POLE_CURRENCY_ID) < 1
                && currencyService.getCurrencyAmount(accountId, FishingConstants.RARE_POLE_CURRENCY_ID) < 1) {
            throw FishingException.insufficientPole();
        }
    }

}
