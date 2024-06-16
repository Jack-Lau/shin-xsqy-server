/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.currency.kuaibi;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.currency.CurrencyChangedEvent;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiConstants.DepositType;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class KuaibiService {

    @Autowired
    private KuaibiRepository kuaibiRepository;
    @Autowired
    private KuaibiDailyRepository kuaibiDailyRepository;

    @Autowired
    private TimeProvider timeProvider;

    private KuaibiRecord getKuaibiRecordForWrite() {
        if (kuaibiRepository.count() < 1) {
            kuaibiRepository.init(new KuaibiRecord());
        }
        return kuaibiRepository.getTheRecordForWrite();
    }

    public KuaibiRecord getKuaibiRecord() {
        if (kuaibiRepository.count() < 1) {
            kuaibiRepository.init(new KuaibiRecord());
        }
        return kuaibiRepository.getTheRecord();
    }

    public KuaibiDailyRecord getLastDayKuaibiDailyRecord() {
        List<KuaibiDailyRecord> records = kuaibiDailyRepository.findAllForWrite();
        if (records.size() > 1) {
            return records.get(records.size() - 2);
        }
        return null;
    }

    public KuaibiDailyRecord getTodayKuaibiDailyRecord() {
        List<KuaibiDailyRecord> records = kuaibiDailyRepository.findAllForWrite();
        if (records.size() > 0) {
            return records.get(records.size() - 1);
        }
        return null;
    }

    private void depositMilliKuaibi(long amount, DepositType depositType) {
        KuaibiRecord kuaibiRecord = getKuaibiRecordForWrite();
        KuaibiDailyRecord todayRecord = getTodayKuaibiDailyRecord();
        //
        if (depositType == DepositType.PLAYER_INTERACTIVE) {
            long maintenanceMilliKuaibi = (long) (amount * KuaibiConstants.DEPOSIT_PLAYER_INTERACTIVE_MAINTENANCE_RATE);
            kuaibiRecord.setMaintenanceMilliKuaibi(kuaibiRecord.getMaintenanceMilliKuaibi() + maintenanceMilliKuaibi);
            long playerInteractiveMilliKuaibi = (long) (amount * KuaibiConstants.DEPOSIT_PLAYER_INTERACTIVE_RATE);
            kuaibiRecord.setAirdropMilliKuaibi(kuaibiRecord.getAirdropMilliKuaibi() + playerInteractiveMilliKuaibi);
            kuaibiRecord.setRebateMilliKuaibiFromPlayerInteractive(kuaibiRecord.getRebateMilliKuaibiFromPlayerInteractive() + amount - maintenanceMilliKuaibi - playerInteractiveMilliKuaibi);
            if (todayRecord != null) {
                todayRecord.setMaintenanceMilliKuaibi(todayRecord.getMaintenanceMilliKuaibi() + maintenanceMilliKuaibi);
                todayRecord.setAirdropMilliKuaibi(todayRecord.getAirdropMilliKuaibi() + playerInteractiveMilliKuaibi);
                todayRecord.setRebateMilliKuaibiFromPlayerInteractive(todayRecord.getRebateMilliKuaibiFromPlayerInteractive() + amount - maintenanceMilliKuaibi - playerInteractiveMilliKuaibi);
            }
        } else {
            long maintenanceMilliKuaibi = (long) (amount * KuaibiConstants.DEPOSIT_MAINTENANCE_RATE);
            kuaibiRecord.setMaintenanceMilliKuaibi(kuaibiRecord.getMaintenanceMilliKuaibi() + maintenanceMilliKuaibi);
            if (todayRecord != null) {
                todayRecord.setMaintenanceMilliKuaibi(todayRecord.getMaintenanceMilliKuaibi() + maintenanceMilliKuaibi);
            }
            long destroyMilliKuaibi = (long) (amount * KuaibiConstants.DEPOSIT_DESTROY_RATE);
            kuaibiRecord.setDestroyMilliKuaibi(kuaibiRecord.getDestroyMilliKuaibi() + destroyMilliKuaibi);
            kuaibiRecord.setRebateMilliKuaibiFromOther(kuaibiRecord.getRebateMilliKuaibiFromOther() + amount - maintenanceMilliKuaibi - destroyMilliKuaibi);
            if (todayRecord != null) {
                todayRecord.setDestroyMilliKuaibi(todayRecord.getDestroyMilliKuaibi() + destroyMilliKuaibi);
                todayRecord.setRebateMilliKuaibiFromOther(todayRecord.getRebateMilliKuaibiFromOther() + amount - maintenanceMilliKuaibi - destroyMilliKuaibi);
            }
        }
        kuaibiRepository.save(kuaibiRecord);
        if (todayRecord != null) {
            kuaibiDailyRepository.save(todayRecord);
        }
    }

    // 返回真正取出的毫块币数量
    public long withdrawMilliKuaibiFromRebate(long amount) {
        KuaibiRecord kuaibiRecord = getKuaibiRecordForWrite();
        long withdrawValue = Math.min(amount, kuaibiRecord.getRebateMilliKuaibiFromPlayerInteractive());
        kuaibiRecord.setRebateMilliKuaibiFromPlayerInteractive(kuaibiRecord.getRebateMilliKuaibiFromPlayerInteractive() - withdrawValue);
        if (withdrawValue < amount) {
            long withdrawFromOther = Math.min(amount - withdrawValue, kuaibiRecord.getRebateMilliKuaibiFromOther());
            kuaibiRecord.setRebateMilliKuaibiFromOther(kuaibiRecord.getRebateMilliKuaibiFromOther() - withdrawFromOther);
            withdrawValue += withdrawFromOther;
        }
        kuaibiRepository.save(kuaibiRecord);
        //return withdrawValue;
        return amount;
    }

    // 返回真正取出的毫块币数量
    public long withdrawMilliKuaibiFromAirdrop(long amount) {
        KuaibiRecord kuaibiRecord = getKuaibiRecordForWrite();
        KuaibiDailyRecord todayRecord = getTodayKuaibiDailyRecord();
        long withdrawValue = Math.min(amount, kuaibiRecord.getAirdropMilliKuaibi());
        kuaibiRecord.setAirdropMilliKuaibi(kuaibiRecord.getAirdropMilliKuaibi() - withdrawValue);
        if (todayRecord != null) {
            todayRecord.setAirdropMilliKuaibi(todayRecord.getAirdropMilliKuaibi() - withdrawValue);
            kuaibiDailyRepository.save(todayRecord);
        }
        kuaibiRepository.save(kuaibiRecord);
        //return withdrawValue;
        return amount;
    }

    @RegisterScheduledTask(cronExpression = "0 55 23 * * ?", executeIfNew = true)
    public void createTodayKuaibiDailyRecord() {
        KuaibiDailyRecord todayRecord = new KuaibiDailyRecord();
        todayRecord.setCreateTime(new Date(timeProvider.currentTime()));
        kuaibiDailyRepository.save(todayRecord);
    }

    @EventListener
    public void onCurrencyChangedEvent(CurrencyChangedEvent event) {
        if (event.getCurrencyId() == CurrencyConstants.ID_毫仙石) {
            if (event.getAfterAmount() < event.getBeforeAmount()) {
                if (!CurrencyConstants.PURPOSE_DECREMENT_FROM_TRANSFER.contains(event.getPurpose())) {
                    long costAmount = event.getBeforeAmount() - event.getAfterAmount();
                    if (CurrencyConstants.PURPOSE_DECREMENT_FROM_PLAYER_INTERACTIVE.contains(event.getPurpose())) {
                        depositMilliKuaibi(costAmount, DepositType.PLAYER_INTERACTIVE);
                    } else {
                        depositMilliKuaibi(costAmount, DepositType.ACTURAL_COST);
                    }
                }
            }
        }
    }

}
