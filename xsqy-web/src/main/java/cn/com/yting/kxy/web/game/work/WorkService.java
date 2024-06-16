/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.work;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.game.work.resource.WorkConfig;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
@Transactional
public class WorkService {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;

    public WorkRecord update(long accountId) {
        WorkRecord workRecord = findOrCreate(accountId);
        if (workRecord.isWorking()) {
            long currentTime = timeProvider.currentTime();
            long lastUpdateTime = workRecord.getLastUpdateTime().toInstant().toEpochMilli();
            long deltaTime = currentTime - lastUpdateTime;
            long deltaMinute = deltaTime / 1000 / 60;
            if (workRecord.getWorkingMinutes() + deltaMinute >= 1440) {
                deltaMinute = 1440 - workRecord.getWorkingMinutes();
            }
            //
            WorkConfig config = resourceContext.getLoader(WorkConfig.class).get(compositePlayerService.getPlayer(accountId).getPlayerLevel());
            workRecord.setWorkingMinutes(workRecord.getWorkingMinutes() + deltaMinute);
            workRecord.setExpAward(workRecord.getExpAward() + deltaMinute * config.getExpPerMinute());
            workRecord.setGoldAward(workRecord.getGoldAward() + deltaMinute * config.getGoldPerMinute());
            workRecord.setLastUpdateTime(new Date());
            workRecord = workRepository.save(workRecord);
        }
        return workRecord;
    }

    public WorkRecord start(long accountId) {
        WorkRecord workRecord = findOrCreate(accountId);
        if (!workRecord.isWorking()) {
            workRecord.setWorking(true);
            workRecord.setWorkingMinutes(0);
            workRecord.setExpAward(0);
            workRecord.setGoldAward(0);
            workRecord.setLastUpdateTime(new Date());
            workRecord = workRepository.save(workRecord);
        }
        return workRecord;
    }

    public WorkRecord end(long accountId) {
        WorkRecord workRecord = findOrCreate(accountId);
        if (workRecord.isWorking()) {
            workRecord.setWorking(false);
            currencyService.increaseCurrency(accountId, CurrencyConstants.ID_经验, workRecord.getExpAward());
            currencyService.increaseCurrency(accountId, CurrencyConstants.ID_元宝, workRecord.getGoldAward());
            workRecord = workRepository.save(workRecord);
        }
        return workRecord;
    }

    private WorkRecord findOrCreate(long accountId) {
        WorkRecord workRecord = workRepository.findByAccountId(accountId);
        if (workRecord == null) {
            workRecord = new WorkRecord();
            workRecord.setAccountId(accountId);
            workRecord.setWorking(false);
            workRecord.setWorkingMinutes(0);
            workRecord.setExpAward(0);
            workRecord.setGoldAward(0);
            workRecord.setLastUpdateTime(new Date());
            workRecord = workRepository.save(workRecord);
        }
        return workRecord;
    }

}
