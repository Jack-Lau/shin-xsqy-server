/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.DefaultTargetSelector;
import cn.com.yting.kxy.battle.robot.RecoverTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 * 普陀山的玩家Ai
 */
public class SchoolRobot_102_Player implements Robot {

    private final long id = 109201;
    private final TargetSelector targetSelector = new DefaultTargetSelector();
    private final TargetSelector recoverTargetSelector = new RecoverTargetSelector();

    public SchoolRobot_102_Player() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        //
        Skill yunhaichaosheng = source.getSkill(102401);
        if (yunhaichaosheng != null) {
            int aliveAllyCount = 0;
            aliveAllyCount = allUnits.stream().filter((u) -> (source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveAllyCount, Integer::sum);
            if (aliveAllyCount < 3) {
                if (RandomProvider.getRandom().nextDouble() < (1 - aliveAllyCount * 0.33)) {
                    return new UseSkillAction(yunhaichaosheng, targetSelector.select(source, allUnits));
                }
            }
        }
        Skill huifengyinlu = source.getSkill(102301);
        if (huifengyinlu != null) {
            int lowHpAllyCount = 0, lessHpAllyCount = 0;
            for (Unit u : allUnits) {
                if (source.isAlly(u) && !u.isHpZero()) {
                    if (u.getHp().getRate() <= 0.8) {
                        lessHpAllyCount++;
                    }
                    if (u.getHp().getRate() <= 0.6) {
                        lowHpAllyCount++;
                    }
                }
            }
            if (lessHpAllyCount >= 3 || lowHpAllyCount >= 2) {
                return new UseSkillAction(huifengyinlu, recoverTargetSelector.select(source, allUnits));
            }
        }
        Skill difanxianlu = source.getSkill(102601);
        if (difanxianlu != null) {
            Unit target = recoverTargetSelector.select(source, allUnits);
            if (target != null && target.getHp().getRate() <= 0.6) {
                return new UseSkillAction(difanxianlu, target);
            }
        }
        return new UseSkillAction(yunhaichaosheng, targetSelector.select(source, allUnits));
    }

    @Override
    public long getId() {
        return this.id;
    }

}
