/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.RandomTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;

/**
 * 宠物Ai
 */
public class MonsterRobot_110014 implements Robot {

    private final long id = 110014;
    private final long baseSkillId = 102201;
    private final TargetSelector targetSelector = new RandomTargetSelector();

    public MonsterRobot_110014() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Skill baseSkill = source.getSkill(baseSkillId);
        Skill furySkill = source.getSkill(102702);
        if (baseSkill != null) {
            int count = 0;
            for (Unit u : allUnits) {
                if (source.isAlly(u) && !u.isHpZero()) {
                    if (u.getHp().getRate() < 0.5) {
                        count++;
                    }
                }
            }
            if (count > 0) {
                Unit target = targetSelector.select(source, allUnits);
                if (target != null) {
                    return new UseSkillAction(baseSkill, target);
                }
            }
        }
        if (furySkill != null) {
            Unit target = targetSelector.select(source, allUnits);
            if (target != null) {
                return new UseSkillAction(furySkill, target);
            }
        }
        return null;
    }

    @Override
    public long getId() {
        return this.id;
    }

}
