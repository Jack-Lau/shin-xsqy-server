/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleDirector;
import java.util.List;

import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.action.SimpleActions;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect.DamageType;
import cn.com.yting.kxy.battle.affect.SimpleAffects;
import cn.com.yting.kxy.battle.event.ActionChanceEvent;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.Skills;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author Darkholme
 */
public class ActionEndHandler implements EventHandler<ActionChanceEvent> {

    public static final long ATTRIBUTE_神佑率_ID = 16L;
    public static final long ATTRIBUTE_免疫死亡_ID = 185L;
    private static final long ATTRIBUTE_行动获得怒气_ID = 20L;
    private static final long ATTRIBUTE_凌霄奇穴5_甲_ID = 132;
    private static final long ATTRIBUTE_凌霄奇穴7_乙_ID = 137;
    private static final long ATTRIBUTE_凌霄奇穴8_乙_ID = 139;
    private static final long ATTRIBUTE_五庄奇穴8_甲_ID = 152;

    public static final long SKILL_凝血刃_ID = 101201;
    public static final long SKILL_鬼神泣_ID = 101401;
    public static final long SKILL_封魂咒_ID = 103301;

    private final double dieSpPunishRate = -0.5;

    @Override
    public EventType<ActionChanceEvent> getHandleEventType() {
        return ActionChanceEvent.ActionChanceEventType.ACTION_END;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void handle(ActionChanceEvent event) {
        Unit actor = event.getActionChance().getActor();
        Action action = event.getActionChance().getAction();
        BattleDirector bd = event.getBattleDirector();
        if (action != null) {
            List<Long> activated凌霄奇穴8甲Ids = new ArrayList<>();
            List<Unit> allUnits = bd.getAllUnits().collect(Collectors.toList());
            for (ActionRecord actionRecord : action.getActionRecords()) {
                //死亡检查
                for (Unit unit : allUnits) {
                    if (unit.isHpZero() && !unit.getIsDead()) {
                        //判断神佑
                        double blessRate = unit.getParameter(ParameterNameConstants.神佑率).getValue();
                        if (unit.getParameter(ParameterNameConstants.免疫死亡).getValue() > 0) {
                            blessRate += 1;
                        }
                        if (actionRecord.actionId == SKILL_凝血刃_ID
                                && actor.getParameter(ParameterNameConstants.凌霄奇穴5_乙).getValue() > 0) {
                            blessRate = 0;
                        }
                        if (RandomProvider.getRandom().nextDouble() < blessRate) {
                            DamageValue recoverValue = DamageValue.hpOnly(Math.max(unit.getHp().getUpperLimit().getValue() * unit.getParameter(ParameterNameConstants.生命回复率).getValue(), 1));
                            SimpleAffects.bless(bd, unit, recoverValue, ATTRIBUTE_神佑率_ID);
                            //
                            if (unit.getParameter(ParameterNameConstants.免疫死亡).getValue() > 0) {
                                SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                                parameterSpace.addParameterBase(ParameterNameConstants.免疫死亡, new SimpleParameterBase(-1));
                                SimpleAffects.attachBuff(bd, unit, unit, 3102016, 100, parameterSpace, ATTRIBUTE_免疫死亡_ID);
                            }
                        } else {
                            Die(event, unit);
                            //
                            if (!actor.isDead()) {
                                if (actionRecord.actionId == SKILL_凝血刃_ID) {
                                    if (actor.getParameter(ParameterNameConstants.凌霄奇穴5_甲).getValue() > 0) {
                                        DamageValue recoverValue = DamageValue
                                                .hpOnly(Math.max(actor.getHp().getUpperLimit().getValue()
                                                        * (0.09 + actor.getParameter(ParameterNameConstants.凌霄奇穴5_甲).getValue() * 0.003)
                                                        * unit.getParameter(ParameterNameConstants.生命回复率).getValue(),
                                                        1));
                                        SimpleAffects.recover(bd, actor, recoverValue, ATTRIBUTE_凌霄奇穴5_甲_ID);
                                    }
                                    if (actor.getParameter(ParameterNameConstants.凌霄奇穴5_乙).getValue() > 0
                                            && RandomProvider.getRandom().nextDouble() < actor.getParameter(ParameterNameConstants.凌霄奇穴5_乙).getValue() * 0.00625) {
                                        SimpleActions.useSkill(bd, actor, unit, Skills.凝血刃);
                                    }
                                } else if (actionRecord.actionId == SKILL_鬼神泣_ID
                                        && actor.getParameter(ParameterNameConstants.凌霄奇穴7_乙).getValue() > 0
                                        && RandomProvider.getRandom().nextDouble() < (0.1 + actor.getParameter(ParameterNameConstants.凌霄奇穴7_乙).getValue() * 0.02)) {
                                    DamageValue spRecoverValue = DamageValue.spOnly(20);
                                    SimpleAffects.recover(bd, actor, spRecoverValue, ATTRIBUTE_凌霄奇穴7_乙_ID);
                                }
                                //
                                if (actor.getParameter(ParameterNameConstants.凌霄奇穴8_乙).getValue() > 0) {
                                    SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                                    parameterSpace.addParameterBase(ParameterNameConstants.速度,
                                            new SimpleParameterBase(0, 0.18 + actor.getParameter(ParameterNameConstants.凌霄奇穴8_乙).getValue() * 0.011));
                                    SimpleAffects.attachBuff(bd, actor, actor, 3102008, 2, parameterSpace, ATTRIBUTE_凌霄奇穴8_乙_ID);
                                }
                                //
                            }
                        }
                    }
                }
                //恢复怒气
                if (actionRecord.executeResult == ActionRecord.ExecuteResult.SUCCESS) {
                    if (!actor.isHpZero() && actor.getParameter(ParameterNameConstants.行动获得怒气).getValue() > 0) {
                        DamageValue spRecoverValue = DamageValue.spOnly(actor.getParameter(ParameterNameConstants.行动获得怒气).getValue());
                        SimpleAffects.recover(bd, actor, spRecoverValue, ATTRIBUTE_行动获得怒气_ID);
                    }
                }
                //action目标触发的效果
                for (List<AffectRecord> recordList : actionRecord.affectRecordPack) {
                    for (AffectRecord record : recordList) {
                        if (!record.target.isHpZero()) {
                            //反击
                            if (record.damageType != null) {
                                if (record.damageType.equals(DamageType.PHYSICS)
                                        && record.isHit
                                        && actor.getParameter(ParameterNameConstants.无视反击).getValue() <= 0) {
                                    if (RandomProvider.getRandom().nextDouble() < record.target.getParameter(ParameterNameConstants.反击率).getValue()) {
                                        SimpleActions.useSkill(bd, record.target, actor, Skills.COUNTER_ATTACK);
                                    }
                                }
                            }
                            //凌霄奇穴8甲
                            if (!activated凌霄奇穴8甲Ids.contains(record.target.getId())
                                    && record.type == AffectRecord.AffectRecordType.DAMAGE
                                    && record.isHit
                                    && record.target.getParameter(ParameterNameConstants.凌霄奇穴8_甲).getValue() > 0
                                    && RandomProvider.getRandom().nextDouble() < (0.05 + record.target.getParameter(ParameterNameConstants.凌霄奇穴8_甲).getValue() * 0.005)) {
                                activated凌霄奇穴8甲Ids.add(record.target.getId());
                                SimpleActions.useSkill(bd, record.target, actor, Skills.ATTACK);
                            }
                        }
                    }
                }
                //五庄奇穴8甲
                if (actionRecord.executeResult == ActionRecord.ExecuteResult.SUCCESS) {
                    if (!actor.isHpZero()
                            && actor.getParameter(ParameterNameConstants.五庄奇穴8_甲).getValue() > 0) {
                        SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                        parameterSpace.addParameterBase(ParameterNameConstants.混元层数, new SimpleParameterBase(1));
                        if (actor.getParameter(ParameterNameConstants.混元层数).getValue() < 3) {
                            parameterSpace.addParameterBase(ParameterNameConstants.法伤,
                                    new SimpleParameterBase(0, 0.00825 + actor.getParameter(ParameterNameConstants.五庄奇穴8_甲).getValue() * 0.00125));
                        }
                        SimpleAffects.attachBuff(bd, actor, actor, 3102009, 3, parameterSpace, ATTRIBUTE_五庄奇穴8_甲_ID);
                    }
                }
                //盘丝奇穴4甲
                if (actionRecord.executeResult == ActionRecord.ExecuteResult.SUCCESS
                        && actionRecord.actionId == SKILL_封魂咒_ID) {
                    Unit target = null;
                    for (List<AffectRecord> recordList : actionRecord.affectRecordPack) {
                        for (AffectRecord record : recordList) {
                            if (record.target != actor && !record.buffs.isEmpty() && !record.isHit) {
                                target = record.target;
                                break;
                            }
                        }
                    }
                    if (!actor.isHpZero()
                            && target != null
                            && actor.getParameter(ParameterNameConstants.盘丝奇穴4_甲).getValue() > 0
                            && RandomProvider.getRandom().nextDouble() < 0.1 + actor.getParameter(ParameterNameConstants.盘丝奇穴4_甲).getValue() * 0.011) {
                        SimpleActions.useSkill(bd, actor, target, Skills.封魂咒);
                    }
                }
                //
            }
        }
    }

    private void Die(ActionChanceEvent event, Unit unit) {
        unit.setDead(true);
        double spPunish = unit.getSp().getValue() * dieSpPunishRate;
        if (unit.getParameter(ParameterNameConstants.凌霄奇穴9_乙).getValue() > 0) {
            spPunish *= (1 - (0.2 + unit.getParameter(ParameterNameConstants.凌霄奇穴9_乙).getValue() * 0.015));
        }
        spPunish = Math.floor(spPunish);
        unit.getSp().shift(spPunish);
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.value.sp = (long) spPunish;
        affectRecord.target = unit;
        if (unit.isFlyable() && !unit.isFlyOut()) {
            unit.setFlyOut(true);
            affectRecord.type = AffectRecord.AffectRecordType.FLY_OUT;
        } else {
            affectRecord.type = AffectRecord.AffectRecordType.DIE;
        }
        event.getBattleDirector().createAffectRecordPack();
        event.getBattleDirector().addAffectRecord(affectRecord);
    }

}
