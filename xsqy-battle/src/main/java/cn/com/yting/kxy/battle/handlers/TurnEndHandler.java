/*
 * Created 2017-3-19 10:54:00
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.SimpleActions;
import cn.com.yting.kxy.battle.affect.SimpleAffects;
import cn.com.yting.kxy.battle.buff.BuffDecayType;
import cn.com.yting.kxy.battle.event.BattleEvent;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
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
public class TurnEndHandler implements EventHandler<BattleEvent> {

    private static final long ATTRIBUTE_回合结束获得怒气_ID = 21;
    private static final long ATTRIBUTE_回春等级_ID = 22;
    private static final long ATTRIBUTE_再生率_ID = 23;
    private static final long ATTRIBUTE_流血值_ID = 32;
    private static final long ATTRIBUTE_普陀奇穴8_甲_ID = 166;
    private static final long ATTRIBUTE_盘丝奇穴5_甲_ID = 174;
    private static final long ATTRIBUTE_霸体_解封_ID = 187;

    @Override
    public EventType<BattleEvent> getHandleEventType() {
        return BattleEvent.BattleEventType.TURN_END;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void handle(BattleEvent event) {
        BattleDirector bd = event.getBattleDirector();
        event.getSource().getAllUnits().forEach(unit -> {
            //恢复怒气
            double increaseSp = 0;
            if (!unit.isHpZero()) {
                increaseSp = unit.getParameter(ParameterNameConstants.回合结束获得怒气).getValue();
                if (unit.getParameter(ParameterNameConstants.普陀奇穴9_甲).getValue() > 0
                        && RandomProvider.getRandom().nextDouble() < (0.3 + unit.getParameter(ParameterNameConstants.普陀奇穴9_甲).getValue() * 0.035)) {
                    increaseSp += 3;
                }
                if (unit.getParameter(ParameterNameConstants.盘丝奇穴9_甲).getValue() > 0
                        && RandomProvider.getRandom().nextDouble() < (0.15 + unit.getParameter(ParameterNameConstants.盘丝奇穴9_甲).getValue() * 0.0175)) {
                    increaseSp += 1 + RandomProvider.getRandom().nextInt(9);
                }
            } else {
                if (unit.getParameter(ParameterNameConstants.五庄奇穴9_乙).getValue() > 0
                        && RandomProvider.getRandom().nextDouble() < (0.4 + unit.getParameter(ParameterNameConstants.五庄奇穴9_乙).getValue() * 0.03)) {
                    increaseSp += 6;
                }
            }
            if (increaseSp > 0) {
                SimpleAffects.recover(bd, unit, DamageValue.spOnly(increaseSp), ATTRIBUTE_回合结束获得怒气_ID);
            }
            //回春等级
            if (!unit.isHpZero() && unit.getParameter(ParameterNameConstants.回春等级).getValue() > 0) {
                int skillLevel = (int) unit.getParameter(ParameterNameConstants.回春等级).getValue();
                int extra基础系数 = (int) (unit.getParameter(ParameterNameConstants.返璞归真强化).getValue() > 0 ? 0.03 : 0);
                SkillParameterTable skillParameter = SkillParameterTable.builder()
                        .元素类型(SkillParam.ElementType.无)
                        .固定命中率(1)
                        .基础命中率(1)
                        .基础系数(0.2 + extra基础系数)
                        .回数系数(0)
                        .基础值等级系数(3)
                        .基础值常数(80)
                        .多目标衰减系数(1)
                        .多目标衰减比例(0)
                        .build();
                SimpleActions.buffAffect_回春(bd, unit, skillParameter, skillLevel, ATTRIBUTE_回春等级_ID);
            }
            //再生率&再生值
            if (!unit.isHpZero() && (unit.getParameter(ParameterNameConstants.再生率).getValue() > 0 || unit.getParameter(ParameterNameConstants.再生值).getValue() > 0)) {
                double recoverValue = unit.getHp().getUpperLimit().getValue() * unit.getParameter(ParameterNameConstants.再生率).getValue()
                        + unit.getParameter(ParameterNameConstants.再生值).getValue();
                SimpleActions.buffAffect_再生(bd, unit, DamageValue.hpOnly(Math.max(recoverValue * unit.getParameter(ParameterNameConstants.生命回复率).getValue(), 1)), ATTRIBUTE_再生率_ID);
            }
            //流血值
            if (!unit.isHpZero() && unit.getParameter(ParameterNameConstants.流血值).getValue() > 0) {
                Unit actor = unit;
                if (unit.getBuff("封魂咒") != null) {
                    actor = unit.getBuff("封魂咒").getActor();
                }
                if (unit.getBuff("万蛊噬心") != null) {
                    actor = unit.getBuff("万蛊噬心").getActor();
                }
                SimpleActions.buffAffect_流血(bd, actor, unit, ATTRIBUTE_流血值_ID);
            }
            //BUFF衰减
            unit.getBuffs().stream()
                    .filter(buff -> buff.getDecayType().equals(BuffDecayType.TURN_END))
                    .collect(Collectors.toList())
                    .forEach(buff -> {
                        SimpleActions.buffDecay(bd, buff, unit);
                    });
            //清除召唤标记
            unit.setSummoned(false);
            //妙手BUFF
            if (!unit.isHpZero() && unit.getParameter(ParameterNameConstants.普陀奇穴8_甲).getValue() > 0) {
                SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
                parameterSpace.addParameterBase(ParameterNameConstants.易疗率,
                        new SimpleParameterBase(0.006 + unit.getParameter(ParameterNameConstants.普陀奇穴8_甲).getValue() * 0.0006));
                //
                SimpleActions.buffAttach(bd, unit, unit, 3102015, 2, parameterSpace, ATTRIBUTE_普陀奇穴8_甲_ID);
            }
            //盘丝奇穴5甲
            if (!unit.isHpZero() && unit.getParameter(ParameterNameConstants.盘丝奇穴5_甲).getValue() > 0) {
                if (RandomProvider.getRandom().nextDouble() < (0.2 + unit.getParameter(ParameterNameConstants.盘丝奇穴5_甲).getValue() * 0.015)) {
                    SimpleActions.buffDetach(bd, unit, unit, ATTRIBUTE_盘丝奇穴5_甲_ID);
                }
            }
            //霸体·极
            if (!unit.isHpZero() && unit.getParameter(ParameterNameConstants.霸体_解封).getValue() > 0) {
                if (RandomProvider.getRandom().nextDouble() < unit.getParameter(ParameterNameConstants.霸体_解封).getValue()) {
                    SimpleActions.buffDetach(bd, unit, unit, ATTRIBUTE_霸体_解封_ID);
                }
            }
            //
        });
    }

}
