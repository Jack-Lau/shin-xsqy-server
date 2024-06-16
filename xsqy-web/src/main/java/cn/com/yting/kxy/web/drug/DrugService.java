/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.drug.resource.DrugInfo;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
@Transactional
public class DrugService implements ParameterSpaceProvider, ResetTask {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    DrugRepository drugRepository;

    @Autowired
    ResourceContext resourceContext;
    @Autowired
    TimeProvider timeProvider;

    public List<DrugRecord> get(long accountId) {
        clean(accountId);
        return drugRepository.findByAccountId(accountId);
    }

    public DrugTakeResult take(long accountId, long currencyId) {
        clean(accountId);
        Long drugId = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 10)
                .filter(it -> it.getId() == currencyId)
                .map(it -> it.getEffectParameter())
                .findAny().orElse(null);
        if (drugId == null) {
            throw KxyWebException.unknown("不能用指定的货币兑换奖励");
        }
        DrugInfo info = resourceContext.getLoader(DrugInfo.class).get(drugId);
        if (info == null) {
            throw KxyWebException.unknown("找不到对应的药品信息");
        }
        //
        DrugRecord record = new DrugRecord();
        record.setAccountId(accountId);
        record.setDrugId(drugId);
        record.setValuePercent_1(Math.max(info.getValue_percent_low(), RandomProvider.getRandom().nextDouble() * info.getValue_percent_high()));
        record.setValuePercent_2(Math.max(info.getValue_percent_low(), RandomProvider.getRandom().nextDouble() * info.getValue_percent_high()));
        record.setExpireTime(Date.from(timeProvider.currentInstant().plus(Duration.ofMinutes(info.getDuration()))));
        if (record.getValuePercent_1() >= info.getValue_percent_high() * 0.95) {
            record.setValuePercent_1(info.getValue_percent_high());
        }
        if (record.getValuePercent_2() >= info.getValue_percent_high() * 0.95) {
            record.setValuePercent_2(info.getValue_percent_high());
        }
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        //
        List<DrugRecord> recordList = drugRepository.findByAccountIdAndDrugIdForWrite(accountId, drugId);
        if (recordList.isEmpty()) {
            record = drugRepository.save(record);
            DrugTakeResult result = new DrugTakeResult(1, info.getName(),
                    info.getAttr_name_1(),
                    info.getAttr_value_1() > 1 ? Math.ceil(info.getAttr_value_1() * record.getValuePercent_1()) : (double) (Math.round(info.getAttr_value_1() * record.getValuePercent_1() * 10000) / 10000.0),
                    info.getAttr_name_2(),
                    info.getAttr_value_2() > 1 ? Math.ceil(info.getAttr_value_2() * record.getValuePercent_2()) : (double) (Math.round(info.getAttr_value_2() * record.getValuePercent_2() * 10000) / 10000.0));
            return result;
        } else {
            long type;
            DrugRecord oldRecord = recordList.get(0);
            if (record.getValuePercent_1() >= oldRecord.getValuePercent_1() * 1.05 || record.getValuePercent_2() >= oldRecord.getValuePercent_2() * 1.05) {
                type = 2;
                oldRecord.setValuePercent_1(record.getValuePercent_1());
                oldRecord.setValuePercent_2(record.getValuePercent_2());
                oldRecord.setExpireTime(record.getExpireTime());
                oldRecord = drugRepository.save(oldRecord);
            } else {
                type = 3;
            }
            DrugTakeResult result = new DrugTakeResult(type, info.getName(),
                    info.getAttr_name_1(),
                    info.getAttr_value_1() > 1 ? Math.ceil(info.getAttr_value_1() * oldRecord.getValuePercent_1()) : (double) (Math.round(info.getAttr_value_1() * oldRecord.getValuePercent_1() * 10000) / 10000.0),
                    info.getAttr_name_2(),
                    info.getAttr_value_2() > 1 ? Math.ceil(info.getAttr_value_2() * oldRecord.getValuePercent_2()) : (double) (Math.round(info.getAttr_value_2() * oldRecord.getValuePercent_2() * 10000) / 10000.0));
            return result;
        }
    }

    public ParameterSpace createPetParameterSpace(long accountId) {
        clean(accountId);
        Map<String, ParameterBase> map = new HashMap<>();
        List<DrugRecord> recordList = drugRepository.findByAccountId(accountId);
        for (DrugRecord record : recordList) {
            DrugInfo info = resourceContext.getLoader(DrugInfo.class).get(record.getDrugId());
            if (info != null && info.getEffect_target() == 2) {
                map.merge(info.getAttr_name_1(),
                        new SimpleParameterBase(info.getAttr_value_1() > 1 ? Math.ceil(info.getAttr_value_1() * record.getValuePercent_1()) : (double) (Math.round(info.getAttr_value_1() * record.getValuePercent_1() * 10000) / 10000.0)), ParameterBase::plus);
                map.merge(info.getAttr_name_2(),
                        new SimpleParameterBase(info.getAttr_value_2() > 1 ? Math.ceil(info.getAttr_value_2() * record.getValuePercent_2()) : (double) (Math.round(info.getAttr_value_2() * record.getValuePercent_2() * 10000) / 10000.0)), ParameterBase::plus);
            }
        }
        return new SimpleParameterSpace(map);
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        Map<String, ParameterBase> map = new HashMap<>();
        List<DrugRecord> recordList = drugRepository.findByAccountId(accountId);
        for (DrugRecord record : recordList) {
            DrugInfo info = resourceContext.getLoader(DrugInfo.class).get(record.getDrugId());
            if (info != null && info.getEffect_target() == 1) {
                map.merge(info.getAttr_name_1(),
                        new SimpleParameterBase(info.getAttr_value_1() > 1 ? Math.ceil(info.getAttr_value_1() * record.getValuePercent_1()) : (double) (Math.round(info.getAttr_value_1() * record.getValuePercent_1() * 10000) / 10000.0)), ParameterBase::plus);
                map.merge(info.getAttr_name_2(),
                        new SimpleParameterBase(info.getAttr_value_2() > 1 ? Math.ceil(info.getAttr_value_2() * record.getValuePercent_2()) : (double) (Math.round(info.getAttr_value_2() * record.getValuePercent_2() * 10000) / 10000.0)), ParameterBase::plus);
            }
        }
        return new SimpleParameterSpace(map);
    }

    @Override
    public void hourlyReset() {
        Instant currentInstant = timeProvider.currentInstant();
        List<DrugRecord> deleteRecordList = new ArrayList<>();
        for (DrugRecord record : drugRepository.findAll()) {
            if (record.getExpireTime().getTime() <= currentInstant.toEpochMilli()) {
                deleteRecordList.add(record);
            }
        }
        drugRepository.deleteAll(deleteRecordList);
    }

    private void clean(long accountId) {
        Instant currentInstant = timeProvider.currentInstant();
        List<DrugRecord> deleteRecordList = new ArrayList<>();
        for (DrugRecord record : drugRepository.findByAccountIdForWrite(accountId)) {
            if (record.getExpireTime().getTime() <= currentInstant.toEpochMilli()) {
                deleteRecordList.add(record);
            }
        }
        drugRepository.deleteAll(deleteRecordList);
    }

}
