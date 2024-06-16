/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.web.currency.CurrencyChangedEvent;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.game.secretShop.SecretShopSharedRepository;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class ChanglefangService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;

    @Autowired
    ChanglefangRepository changlefangRepository;
    @Autowired
    ChanglefangSharedRepository changlefangSharedRepository;
    @Autowired
    ChanglefangLogRepository changlefangLogRepository;
    @Autowired
    SecretShopSharedRepository secretShopSharedRepository;

    @Autowired
    TimeProvider timeProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (changlefangSharedRepository.count() < 1) {
            changlefangSharedRepository.init(new ChanglefangSharedRecord());
        }
        ChanglefangSharedRecord sharedRecord = changlefangSharedRepository.getTheRecord();
        if (sharedRecord.getLastDayAwardEnergy() == null) {
            sharedRecord.setLastDayAwardEnergy(sharedRecord.getDayEnergy() / 100);
            changlefangSharedRepository.save(sharedRecord);
        }
    }

    public ChanglefangOverall get(long accountId) {
        return new ChanglefangOverall(changlefangSharedRepository.getTheRecord(), findOrCreate(accountId));
    }

    public List<ChanglefangLog> log(long accountId) {
        return changlefangLogRepository.findByAccountId(accountId);
    }

    public ChanglefangOverall buy(long accountId, int amount) {
        ChanglefangRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (amount < 1) {
            throw ChanglefangException.至少购买1张本票();
        }
        long milliKcCost = ChanglefangConstants.MILLI_KC_COST_PER_SHARE * amount;
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, milliKcCost, true, CurrencyConstants.PURPOSE_DECREMENT_长乐坊购买本票);
        //
        record.setDayShare(record.getDayShare() + amount);
        record = changlefangRepository.save(record);
        ChanglefangSharedRecord sr = changlefangSharedRepository.getTheRecordForWrite();
        sr.setDayShare(sr.getDayShare() + amount);
        sr.setDayEnergy(sr.getDayEnergy() + ChanglefangConstants.DAY_ENERGY_ADD_PER_SHARE * amount);
        sr = changlefangSharedRepository.save(sr);
        //
        ChanglefangLog log = new ChanglefangLog();
        log.setAccountId(accountId);
        log.setType("BUY");
        log.setCostValue(milliKcCost);
        log.setGainValue(amount);
        log.setCreateTime(new Date(timeProvider.currentTime()));
        changlefangLogRepository.save(log);
        return new ChanglefangOverall(sr, record);
    }

    public ChanglefangOverall exchange_kc(long accountId, int amount) {
        ChanglefangRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (amount < 1) {
            throw ChanglefangException.至少兑换1个块币();
        }
        long energyCost = ChanglefangConstants.ENERGY_COST_PER_KC * amount;
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_坊金, energyCost, true, CurrencyConstants.PURPOSE_DECREMENT_长乐坊购买本票);
        //
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_毫仙石, KuaibiUnits.fromKuaibi(amount), CurrencyConstants.PURPOSE_INCREMENT_长乐坊兑换块币);
        //
        ChanglefangLog log = new ChanglefangLog();
        log.setAccountId(accountId);
        log.setType("EXCHANGE_KC");
        log.setCostValue(energyCost);
        log.setGainValue(amount);
        log.setCreateTime(new Date(timeProvider.currentTime()));
        changlefangLogRepository.save(log);
        return new ChanglefangOverall(changlefangSharedRepository.getTheRecord(), record);
    }

    public long lastDayAwardEnergy() {
        return changlefangSharedRepository.getTheRecord().getLastDayAwardEnergy();
    }

    public long changlefangShare(long accountId) {
        ChanglefangRecord record = findOrCreate(accountId);
        return record.getTotalShare() + record.getDayShare();
    }

    @Override
    public void dailyReset() {
        ChanglefangSharedRecord sr = changlefangSharedRepository.getTheRecordForWrite();
        List<ChanglefangRecord> records = changlefangRepository.findAll();
        List<ChanglefangLog> logs = new ArrayList<>();
        long dayEnergy = sr.getDayEnergy();
        long awardEnergy = dayEnergy / 100;
        for (ChanglefangRecord record : records) {
            long gainEnergy = (long) (awardEnergy * ((double) record.getTotalShare() / (double) sr.getTotalShare()));
            if (gainEnergy > 0) {
                currencyService.increaseCurrency(record.getAccountId(), CurrencyConstants.ID_坊金, gainEnergy, CurrencyConstants.PURPOSE_INCREMENT_长乐坊兑换块币);
                sr.setDayEnergy(sr.getDayEnergy() - gainEnergy);
                //
                ChanglefangLog log = new ChanglefangLog();
                log.setAccountId(record.getAccountId());
                log.setType("BONUS");
                log.setCostValue(0);
                log.setGainValue(gainEnergy);
                log.setCreateTime(new Date(timeProvider.currentTime()));
                logs.add(log);
            }
            record.setTotalShare(record.getTotalShare() + record.getDayShare());
            record.setDayShare(0);
        }
        sr.setTotalShare(sr.getTotalShare() + sr.getDayShare());
        sr.setDayShare(0);
        sr.setLastDayAwardEnergy(awardEnergy);
        changlefangSharedRepository.save(sr);
        changlefangRepository.saveAll(records);
        changlefangLogRepository.saveAll(logs);
    }

    @EventListener
    public void onCurrencyChangedEvent(CurrencyChangedEvent event) {
        if (event.getAfterAmount() < event.getBeforeAmount()) {
            if (ChanglefangConstants.增加能量的消耗源.contains(event.getPurpose())) {
                long costAmount = 0;
                if (event.getCurrencyId() == CurrencyConstants.ID_毫仙石) {
                    costAmount = event.getBeforeAmount() - event.getAfterAmount();
                }
//                if (event.getCurrencyId() == CurrencyConstants.ID_玉石) {
//                    costAmount = (long) ((event.getBeforeAmount() - event.getAfterAmount()) * (1000 / (double) secretShopSharedRepository.getTheRecord().getKcPackPrice()) * 1000);
//                }
                ChanglefangSharedRecord sr = changlefangSharedRepository.getTheRecordForWrite();
                sr.setDayEnergy((long) (sr.getDayEnergy() + ChanglefangConstants.DAY_ENERGY_ADD_RATE * costAmount));
                changlefangSharedRepository.save(sr);
            }
        }
    }

    private ChanglefangRecord findOrCreate(long accountId) {
        ChanglefangRecord record = changlefangRepository.findByAccountIdForWrite(accountId).orElse(null);
        if (record == null) {
            record = new ChanglefangRecord();
            record.setAccountId(accountId);
            record.setDayShare(0);
            record.setTotalShare(0);
            record = changlefangRepository.save(record);
        }
        return record;
    }

    private void checkAvailable() {
        if (!ChanglefangConstants.AVAILABLE) {
            throw ChanglefangException.玩法未开启();
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < ChanglefangConstants.PLAYER_LEVEL_REQUIRE) {
            throw ChanglefangException.角色等级不足();
        }
    }

}
