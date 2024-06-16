/*
 * Created 2017-3-17 17:15:20
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect.DamageType;
import cn.com.yting.kxy.battle.affect.SimpleAffects;
import cn.com.yting.kxy.battle.event.DamageEvent;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

import java.util.stream.Collectors;

/**
 *
 * @author Azige
 */
public class DamageActivateHandler implements EventHandler<DamageEvent> {

    private static final long ATTRIBUTE_受伤怒气回复率_ID = 19L;
    private static final long ATTRIBUTE_吸血率_ID = 12L;
    private static final long ATTRIBUTE_反震率_ID = 31L;
    private static final long ATTRIBUTE_五庄奇穴9_甲_ID = 154;

    public static final long SKILL_风驰_ID = 3101220;
    public static final long SKILL_风庭漂移_ID = 3101250;
    public static final long SKILL_灼岩爪_ID = 3101230;
    public static final long SKILL_熔岩烈爪_ID = 3101260;
    public static final long SKILL_四边静_ID = 101101;
    public static final long SKILL_鬼神泣_ID = 101401;
    public static final long SKILL_八方湮灭_ID = 104701;

    public static final long BUFF_风驰_ID = 3102004;
    public static final long BUFF_风庭漂移_ID = 3102006;
    public static final long BUFF_减速_ID = 3102007;

    private final double 受伤怒气回复因子 = 50;
    private final double 受伤怒气回复常数 = 1;

    @Override
    public EventType<DamageEvent> getHandleEventType() {
        return DamageEvent.DamageEventType.AFFECT_DAMAGE;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void handle(DamageEvent event) {
        AffectRecord record = event.getRecord();
        BattleDirector bd = event.getBattleDirector();
        if (record.isHit) {
            //actor恢复怒气
            if (record.actor != null && record.actor instanceof Unit) {
                Unit actorUnit = (Unit) record.actor;
                if (!actorUnit.isHpZero()
                        && actorUnit.getParameter(ParameterNameConstants.五庄奇穴9_甲).getValue() > 0
                        && RandomProvider.getRandom().nextDouble() < (0.25 + actorUnit.getParameter(ParameterNameConstants.五庄奇穴9_甲).getValue() * 0.025)) {
                    DamageValue spRecoverValue = DamageValue.spOnly(1);
                    SimpleAffects.recover(bd, actorUnit, spRecoverValue, ATTRIBUTE_五庄奇穴9_甲_ID);
                }
            }
            //target恢复怒气
            if (!record.target.isHpZero()) {
                double damagePercent = (double) record.value.hp / record.target.getHp().getUpperLimit().getValue();
                if (damagePercent > 0) {
                    double finalSpRecover = Math.floor(受伤怒气回复因子 * damagePercent + 受伤怒气回复常数);
                    finalSpRecover *= record.target.getParameter(ParameterNameConstants.受伤怒气回复率).getValue();
                    //
                    if (record.sourceId == SKILL_八方湮灭_ID && record.actor != null && record.actor instanceof Unit) {
                        Unit actorUnit = (Unit) record.actor;
                        if (actorUnit.getParameter(ParameterNameConstants.五庄奇穴7_乙).getValue() > 0) {
                            finalSpRecover *= (1 - (0.5 + actorUnit.getParameter(ParameterNameConstants.五庄奇穴7_乙).getValue() * 0.025));
                        }
                    }
                    //
                    if (record.target.getParameter(ParameterNameConstants.凌霄奇穴9_甲).getValue() > 0
                            && RandomProvider.getRandom().nextDouble() < (0.15 + record.target.getParameter(ParameterNameConstants.凌霄奇穴9_甲).getValue() * 0.015)) {
                        finalSpRecover += 3;
                    }
                    if (record.isBlock
                            && record.target.getParameter(ParameterNameConstants.普陀奇穴9_乙).getValue() > 0
                            && RandomProvider.getRandom().nextDouble() < (0.5 + record.target.getParameter(ParameterNameConstants.普陀奇穴9_乙).getValue() * 0.025)) {
                        finalSpRecover += 10;
                    }
                    //
                    DamageValue spRecoverValue = DamageValue.spOnly(finalSpRecover);
                    SimpleAffects.recover(bd, record.target, spRecoverValue, ATTRIBUTE_受伤怒气回复率_ID);
                }
            }
            //吸血
            if (!record.isOverKill) {
                if (record.actor != null && record.actor instanceof Unit) {
                    Unit actorUnit = (Unit) record.actor;
                    if (!actorUnit.isHpZero()) {
                        DamageValue drainBloodValue = null;
                        if (record.damageType == DamageType.PHYSICS) {
                            double finalDrainBloodRate = actorUnit.getParameter(ParameterNameConstants.吸血率).getValue();
                            if (record.sourceId == SKILL_鬼神泣_ID) {
                                finalDrainBloodRate += actorUnit.getParameter(ParameterNameConstants.凌霄奇穴7_甲).getValue() > 0
                                        ? 0.36 + actorUnit.getParameter(ParameterNameConstants.凌霄奇穴7_甲).getValue() * 0.007 : 0;
                            }
                            //
                            if (finalDrainBloodRate > 0) {
                                finalDrainBloodRate *= actorUnit.getParameter(ParameterNameConstants.生命回复率).getValue();
                                double extraSp = 0;
                                if (actorUnit.getParameter(ParameterNameConstants.盘丝奇穴9_乙).getValue() > 0) {
                                    extraSp = RandomProvider.getRandom().nextDouble() < (0.3 + actorUnit.getParameter(ParameterNameConstants.盘丝奇穴9_乙).getValue() * 0.035) ? 2 : 0;
                                }
                                drainBloodValue = new DamageValue(Math.max(record.value.hp * finalDrainBloodRate, 1), extraSp);
                            }
                        }
                        if (drainBloodValue != null) {
                            SimpleAffects.recover(bd, actorUnit, drainBloodValue, ATTRIBUTE_吸血率_ID);
                        }
                    }
                }
            }
            //反震
            if (!record.target.isHpZero() && record.actor instanceof Unit) {
                Unit actorUnit = (Unit) record.actor;
                if (record.damageType.equals(DamageType.MAGIC)
                        && actorUnit.getParameter(ParameterNameConstants.无视反震).getValue() <= 0) {
                    if (RandomProvider.getRandom().nextDouble() < record.target.getParameter(ParameterNameConstants.反震率).getValue()) {
                        DamageValue damageValue = DamageValue.hpOnly(record.value.hp / 2);
                        SimpleAffects.damage(bd, record.target, actorUnit, damageValue, ATTRIBUTE_反震率_ID);
                    }
                }
            }
            //灼岩爪 & 熔岩烈爪
            if (record.target.isHpZero() && record.actor instanceof Unit) {
                Unit actorUnit = (Unit) record.actor;
                if (record.sourceId == SKILL_灼岩爪_ID || record.sourceId == SKILL_熔岩烈爪_ID) {
                    Unit targetUnit = null;
                    for (Unit u : event.getBattleDirector().getRivals(actorUnit)) {
                        if (!u.isHpZero()) {
                            if (targetUnit == null || u.getHp().getValue() < targetUnit.getHp().getValue()) {
                                targetUnit = u;
                            }
                        }
                    }
                    if (targetUnit != null) {
                        UseSkillAction action;
                        if (record.sourceId == SKILL_灼岩爪_ID) {
                            action = new UseSkillAction(actorUnit.getSkill(SKILL_灼岩爪_ID), targetUnit);
                        } else {
                            action = new UseSkillAction(actorUnit.getSkill(SKILL_熔岩烈爪_ID), targetUnit);
                        }
                        action.perform(actorUnit, event.getBattleDirector(), event.getBattleDirector().getAllUnits().collect(Collectors.toList()));
                    }
                }
            }
            //风驰 & 风庭漂移
            if (!record.target.isHpZero() && record.actor instanceof Unit) {
                Unit actorUnit = (Unit) record.actor;
                if (record.sourceId == SKILL_风驰_ID || record.sourceId == SKILL_风庭漂移_ID) {
                    SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                    parameterSpace.addParameterBase(ParameterNameConstants.速度, new SimpleParameterBase(0, -0.05));
                    SimpleAffects.attachBuff(bd, actorUnit, record.target, BUFF_风驰_ID, 3, parameterSpace, SKILL_风驰_ID);
                    if (record.sourceId == SKILL_风庭漂移_ID) {
                        parameterSpace = new SimpleParameterSpace();
                        parameterSpace.addParameterBase(ParameterNameConstants.速度, new SimpleParameterBase(0, -0.03));
                        SimpleAffects.attachBuff(bd, actorUnit, record.target, BUFF_风庭漂移_ID, 3, parameterSpace, SKILL_风庭漂移_ID);
                    }
                }
            }
            //凌霄奇穴6_甲
            if (!record.target.isHpZero() && record.actor instanceof Unit) {
                Unit actorUnit = (Unit) record.actor;
                if (record.sourceId == SKILL_四边静_ID
                        && record.isMainTarget
                        && actorUnit.getParameter(ParameterNameConstants.凌霄奇穴6_甲).getValue() > 0) {
                    SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                    parameterSpace.addParameterBase(ParameterNameConstants.速度,
                            new SimpleParameterBase(0, -0.05 + actorUnit.getParameter(ParameterNameConstants.凌霄奇穴6_甲).getValue() * -0.005));
                    SimpleAffects.attachBuff(bd, actorUnit, record.target, BUFF_减速_ID, 3, parameterSpace, SKILL_四边静_ID);
                }
            }
            //
        } else {
            //
        }
    }

}
