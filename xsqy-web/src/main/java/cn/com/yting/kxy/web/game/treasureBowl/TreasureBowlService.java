/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.game.changlefang.ChanglefangService;
import cn.com.yting.kxy.web.game.treasureBowl.resource.ChangleTokenFactor;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
@Transactional
public class TreasureBowlService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;
    @Autowired
    ChanglefangService changlefangService;

    @Autowired
    TreasureBowlRepository treasureBowlRepository;
    @Autowired
    TreasureBowlAttendRepository treasureBowlAttendRepository;
    @Autowired
    TreasureBowlSelfRepository treasureBowlSelfRepository;

    @Autowired
    ResourceContext resourceContext;
    @Autowired
    TimeProvider timeProvider;

    RandomSelector<Long> factorJackpot;
    TreasureBowlRecord currentTreasureBowl;

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder factorJackpotBuilder = RandomSelector.<Long>builder();
        List<ChangleTokenFactor> changleTokenFactor = new ArrayList<>(resourceContext.getLoader(ChangleTokenFactor.class).getAll().values());
        changleTokenFactor.forEach((ctf) -> {
            factorJackpotBuilder.add(ctf.getId(), ctf.getProbability());
        });
        factorJackpot = factorJackpotBuilder.build(RandomSelectType.DEPENDENT);
        //
        currentTreasureBowl = null;
        for (TreasureBowlRecord bowlRecord : treasureBowlRepository.findAll()) {
            if (!bowlRecord.isFinish()) {
                currentTreasureBowl = bowlRecord;
            }
        }
    }

    public TreasureBowlOverall get(long accountId) {
        if (currentTreasureBowl == null) {
            return new TreasureBowlOverall(new TreasureBowl(null, null), findOrCreate(accountId));
        } else {
            return new TreasureBowlOverall(new TreasureBowl(currentTreasureBowl, treasureBowlAttendRepository.findByTreasureBowlId(currentTreasureBowl.getId())), findOrCreate(accountId));
        }
    }

    public List<TreasureBowl> today() {
        List<TreasureBowl> result = new ArrayList<>();
        for (TreasureBowlRecord record : treasureBowlRepository.findAll()) {
            result.add(new TreasureBowl(record, treasureBowlAttendRepository.findByTreasureBowlId(record.getId())));
        }
        return result;
    }

    public TreasureBowlOverall attend(long accountId) {
        checkLevelAndChanglefangShare(accountId);
        checkToken(accountId);
        //
        if (currentTreasureBowl == null || currentTreasureBowl.isFinish() || !TreasureBowlConstants.VALID_PERIOD_GAME.isValid(timeProvider.currentInstant())) {
            throw TreasureBowlException.notStartedYet();
        }
        TreasureBowlSelfRecord selfRecord = findOrCreate(accountId);
        if (selfRecord.getTodayCost() >= TreasureBowlConstants.MAX_TOKEN_USE_PER_DAY) {
            throw TreasureBowlException.todayTokenReachLimit();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_长乐贡牌, 1, true, CurrencyConstants.PURPOSE_DECREMENT_长乐聚宝盆);
        //
        TreasureBowlAttendRecord attendRecord = treasureBowlAttendRepository.findByAccountIdAndTreasureBowlId(accountId, currentTreasureBowl.getId());
        if (attendRecord == null) {
            attendRecord = new TreasureBowlAttendRecord();
            attendRecord.setTreasureBowlId(currentTreasureBowl.getId());
            attendRecord.setAccountId(accountId);
            attendRecord.setPlayerName(compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName());
            attendRecord.setTotalChangleToken(0);
            attendRecord.setTotalContribution(0);
            attendRecord.setTotalAward(0);
        }
        long factor = Math.max(resourceContext.getLoader(ChangleTokenFactor.class).get(factorJackpot.getSingle()).getFactor(), 1);
        long contribution = (long) (Math.pow((double) compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc() / 1000.0, 3.0) / 1000.0 * (double) factor);
        if (factor >= 10) {
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    TreasureBowlConstants.BIG_FACTOR_BROADCAST,
                    ImmutableMap.of(
                            "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                            "factor", factor,
                            "contribution", contribution)
            ));
        }
        attendRecord.setTotalChangleToken(attendRecord.getTotalChangleToken() + 1);
        attendRecord.setTotalContribution(attendRecord.getTotalContribution() + contribution);
        attendRecord = treasureBowlAttendRepository.save(attendRecord);
        //
        selfRecord.setTotalCost(selfRecord.getTotalCost() + 1);
        selfRecord.setTodayCost(selfRecord.getTodayCost() + 1);
        selfRecord.setLastAddContribution(contribution);
        treasureBowlSelfRepository.save(selfRecord);
        //
        return get(accountId);
    }

    public TreasureBowlOverall take(long accountId) {
        checkLevelAndChanglefangShare(accountId);
        TreasureBowlSelfRecord selfRecord = findOrCreate(accountId);
        if (selfRecord.getNotTakeAmount() <= 0) {
            throw TreasureBowlException.noAward();
        }
        currencyService.increaseCurrency(accountId,
                CurrencyConstants.ID_坊金,
                selfRecord.getNotTakeAmount(),
                CurrencyConstants.PURPOSE_INCREMENT_长乐聚宝盆);
        selfRecord.setLastTakeAmount(selfRecord.getNotTakeAmount());
        selfRecord.setNotTakeAmount(0);
        treasureBowlSelfRepository.save(selfRecord);
        return get(accountId);
    }

    @RegisterScheduledTask(cronExpression = "10 0 * * * ?", executeIfNew = true)
    public void award() {
        if (currentTreasureBowl != null && !currentTreasureBowl.isFinish()) {
            currentTreasureBowl.setFinish(true);
            currentTreasureBowl = treasureBowlRepository.save(currentTreasureBowl);
            //
            List<TreasureBowlAttendRecord> attendRecords = treasureBowlAttendRepository.findByTreasureBowlId(currentTreasureBowl.getId());
            long totalContribution = 0;
            for (TreasureBowlAttendRecord attendRecord : attendRecords) {
                totalContribution += attendRecord.getTotalContribution();
            }
            for (TreasureBowlAttendRecord attendRecord : attendRecords) {
                long gainAward = (long) (currentTreasureBowl.getTotalAward() * ((double) attendRecord.getTotalContribution() / (double) totalContribution));
                attendRecord.setTotalAward(gainAward);
                TreasureBowlSelfRecord selfRecord = findOrCreate(attendRecord.getAccountId());
                selfRecord.setTotalGain(selfRecord.getTotalGain() + gainAward);
                selfRecord.setNotTakeAmount(selfRecord.getNotTakeAmount() + gainAward);
                treasureBowlSelfRepository.save(selfRecord);
            }
            treasureBowlAttendRepository.saveAll(attendRecords);
        }
        //
        if (TreasureBowlConstants.VALID_PERIOD_GAME.isValid(timeProvider.currentInstant())) {
            currentTreasureBowl = new TreasureBowlRecord();
            currentTreasureBowl.setTotalAward(changlefangService.lastDayAwardEnergy() / 12);
            currentTreasureBowl.setFinish(false);
            currentTreasureBowl = treasureBowlRepository.save(currentTreasureBowl);
        }
    }

    @Override
    public void dailyReset() {
        List<TreasureBowlRecord> deleteBowlRecords = new ArrayList<>();
        List<TreasureBowlAttendRecord> deleteAttendRecords = new ArrayList<>();
        for (TreasureBowlRecord bowlRecord : treasureBowlRepository.findAll()) {
            if (bowlRecord.isFinish()) {
                deleteBowlRecords.add(bowlRecord);
                deleteAttendRecords.addAll(treasureBowlAttendRepository.findByTreasureBowlId(bowlRecord.getId()));
            }
        }
        treasureBowlRepository.deleteAll(deleteBowlRecords);
        treasureBowlAttendRepository.deleteAll(deleteAttendRecords);
        //
        List<TreasureBowlSelfRecord> selfRecords = treasureBowlSelfRepository.findAll();
        for (TreasureBowlSelfRecord selfRecord : selfRecords) {
            selfRecord.setTodayCost(0);
        }
        treasureBowlSelfRepository.saveAll(selfRecords);
    }

    private TreasureBowlSelfRecord findOrCreate(long accountId) {
        TreasureBowlSelfRecord selfRecord = treasureBowlSelfRepository.findByAccountId(accountId);
        if (selfRecord == null) {
            selfRecord = new TreasureBowlSelfRecord();
            selfRecord.setAccountId(accountId);
            selfRecord.setTotalCost(0);
            selfRecord.setTotalGain(0);
            selfRecord.setNotTakeAmount(0);
            selfRecord = treasureBowlSelfRepository.save(selfRecord);
        }
        return selfRecord;
    }

    private void checkLevelAndChanglefangShare(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < TreasureBowlConstants.PLAYER_LEVEL_REQUIRE) {
            throw TreasureBowlException.insufficientPlayerLevel();
        }
        if (changlefangService.changlefangShare(accountId) < TreasureBowlConstants.CHANGLEFANG_SHARE_REQUIRE) {
            throw TreasureBowlException.insufficientChanglefangShare();
        }
    }

    private void checkToken(long accountId) {
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_长乐贡牌) < 1) {
            throw TreasureBowlException.insufficientChangleToken();
        }
    }

}
