/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.cultivation;

import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.cultivation.resource.CultivationConsumption;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import java.util.HashMap;
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
public class CultivationService implements ParameterSpaceProvider {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;

    @Autowired
    CultivationRepository cultivationRepository;

    @Autowired
    ResourceContext resourceContext;

    public CultivationRecord get(long accountId) {
        return findOrCreate(accountId);
    }

    public CultivationRecord make(long accountId, int cultivationIndex, long amountToConsume) {
        checkPlayerLevel(accountId);
        CultivationRecord cultivationRecord = findOrCreate(accountId);
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_金坷垃);
        if (currencyRecord.getAmount() < amountToConsume) {
            throw CultivationException.insufficientCurrency();
        }
        switch (cultivationIndex) {
            case 1: {
                if (!resourceContext.getLoader(CultivationConsumption.class).exists(cultivationRecord.getPlayerAtkLevel())) {
                    throw CultivationException.cultivationLevelReachLimit();
                }
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_金坷垃, amountToConsume);
                cultivationRecord.setPlayerAtkCurrentExp((int) (cultivationRecord.getPlayerAtkCurrentExp() + amountToConsume * 10));
                //
                CultivationConsumption cultivationConsumption = resourceContext.getLoader(CultivationConsumption.class).get(cultivationRecord.getPlayerAtkLevel());
                if (cultivationRecord.getPlayerAtkCurrentExp() >= cultivationConsumption.getPlayer_atk_exp()) {
                    cultivationRecord.setPlayerAtkCurrentExp(cultivationRecord.getPlayerAtkCurrentExp() - cultivationConsumption.getPlayer_atk_exp());
                    cultivationRecord.setPlayerAtkLevel(cultivationRecord.getPlayerAtkLevel() + 1);
                }
                break;
            }
            case 2: {
                if (!resourceContext.getLoader(CultivationConsumption.class).exists(cultivationRecord.getPlayerDefLevel())) {
                    throw CultivationException.cultivationLevelReachLimit();
                }
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_金坷垃, amountToConsume);
                cultivationRecord.setPlayerDefCurrentExp((int) (cultivationRecord.getPlayerDefCurrentExp() + amountToConsume * 10));
                //
                CultivationConsumption cultivationConsumption = resourceContext.getLoader(CultivationConsumption.class).get(cultivationRecord.getPlayerDefLevel());
                if (cultivationRecord.getPlayerDefCurrentExp() >= cultivationConsumption.getPlayer_def_exp()) {
                    cultivationRecord.setPlayerDefCurrentExp(cultivationRecord.getPlayerDefCurrentExp() - cultivationConsumption.getPlayer_def_exp());
                    cultivationRecord.setPlayerDefLevel(cultivationRecord.getPlayerDefLevel() + 1);
                }
                break;
            }
            case 3: {
                if (!resourceContext.getLoader(CultivationConsumption.class).exists(cultivationRecord.getPetAtkLevel())) {
                    throw CultivationException.cultivationLevelReachLimit();
                }
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_金坷垃, amountToConsume);
                cultivationRecord.setPetAtkCurrentExp((int) (cultivationRecord.getPetAtkCurrentExp() + amountToConsume * 10));
                //
                CultivationConsumption cultivationConsumption = resourceContext.getLoader(CultivationConsumption.class).get(cultivationRecord.getPetAtkLevel());
                if (cultivationRecord.getPetAtkCurrentExp() >= cultivationConsumption.getPet_atk_exp()) {
                    cultivationRecord.setPetAtkCurrentExp(cultivationRecord.getPetAtkCurrentExp() - cultivationConsumption.getPet_atk_exp());
                    cultivationRecord.setPetAtkLevel(cultivationRecord.getPetAtkLevel() + 1);
                }
                break;
            }
            case 4: {
                if (!resourceContext.getLoader(CultivationConsumption.class).exists(cultivationRecord.getPetDefLevel())) {
                    throw CultivationException.cultivationLevelReachLimit();
                }
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_金坷垃, amountToConsume);
                cultivationRecord.setPetDefCurrentExp((int) (cultivationRecord.getPetDefCurrentExp() + amountToConsume * 10));
                //
                CultivationConsumption cultivationConsumption = resourceContext.getLoader(CultivationConsumption.class).get(cultivationRecord.getPetDefLevel());
                if (cultivationRecord.getPetDefCurrentExp() >= cultivationConsumption.getPet_def_exp()) {
                    cultivationRecord.setPetDefCurrentExp(cultivationRecord.getPetDefCurrentExp() - cultivationConsumption.getPet_def_exp());
                    cultivationRecord.setPetDefLevel(cultivationRecord.getPetDefLevel() + 1);
                }
                break;
            }
            case 5: {
                if (!resourceContext.getLoader(CultivationConsumption.class).exists(cultivationRecord.getPlayerReviveLevel())) {
                    throw CultivationException.cultivationLevelReachLimit();
                }
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_金坷垃, amountToConsume);
                cultivationRecord.setPlayerReviveCurrentExp((int) (cultivationRecord.getPlayerReviveCurrentExp() + amountToConsume * 10));
                //
                CultivationConsumption cultivationConsumption = resourceContext.getLoader(CultivationConsumption.class).get(cultivationRecord.getPlayerReviveLevel());
                if (cultivationRecord.getPlayerReviveCurrentExp() >= cultivationConsumption.getPlayer_revive_exp()) {
                    cultivationRecord.setPlayerReviveCurrentExp(cultivationRecord.getPlayerReviveCurrentExp() - cultivationConsumption.getPlayer_revive_exp());
                    cultivationRecord.setPlayerReviveLevel(cultivationRecord.getPlayerReviveLevel() + 1);
                }
                break;
            }
        }
        return cultivationRepository.save(cultivationRecord);
    }

    private CultivationRecord findOrCreate(long accountId) {
        CultivationRecord cultivationRecord = cultivationRepository.findByAccountId(accountId);
        if (cultivationRecord == null) {
            cultivationRecord = new CultivationRecord();
            cultivationRecord.setAccountId(accountId);
            cultivationRecord.setPlayerAtkLevel(0);
            cultivationRecord.setPlayerAtkCurrentExp(0);
            cultivationRecord.setPlayerDefLevel(0);
            cultivationRecord.setPlayerDefCurrentExp(0);
            cultivationRecord.setPetAtkLevel(0);
            cultivationRecord.setPetAtkCurrentExp(0);
            cultivationRecord.setPetDefLevel(0);
            cultivationRecord.setPetDefCurrentExp(0);
            cultivationRecord.setPlayerReviveLevel(0);
            cultivationRecord.setPlayerReviveCurrentExp(0);
            cultivationRecord = cultivationRepository.save(cultivationRecord);
        }
        return cultivationRecord;
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < CultivationConstants.PLAYER_LEVEL_REQUIRE) {
            throw CultivationException.insufficientPlayerLevel();
        }
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        Map<String, ParameterBase> map = new HashMap<>();
        CultivationRecord cultivationRecord = findOrCreate(accountId);
        //
        map.merge(ParameterNameConstants.招式力, new SimpleParameterBase(cultivationRecord.getPlayerAtkLevel() * 0.001), ParameterBase::plus);
        map.merge(ParameterNameConstants.抵抗力, new SimpleParameterBase(cultivationRecord.getPlayerDefLevel() * 0.001), ParameterBase::plus);
        map.merge(ParameterNameConstants.御兽招式力, new SimpleParameterBase(cultivationRecord.getPetAtkLevel() * 0.001), ParameterBase::plus);
        map.merge(ParameterNameConstants.御兽抵抗力, new SimpleParameterBase(cultivationRecord.getPetDefLevel() * 0.001), ParameterBase::plus);
        map.merge(ParameterNameConstants.神佑率, new SimpleParameterBase(cultivationRecord.getPlayerReviveLevel() * 0.0004), ParameterBase::plus);
        //
        double fc = 10 * (cultivationRecord.getPlayerAtkLevel() + cultivationRecord.getPlayerDefLevel()
                + cultivationRecord.getPetAtkLevel() + cultivationRecord.getPetDefLevel()
                + cultivationRecord.getPlayerReviveLevel());
        map.put(ParameterNameConstants.战斗力, new SimpleParameterBase(fc));
        //
        return new SimpleParameterSpace(map);
    }

}
