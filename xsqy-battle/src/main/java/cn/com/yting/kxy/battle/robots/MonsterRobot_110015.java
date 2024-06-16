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
public class MonsterRobot_110015 implements Robot {

    private final long id = 110015;
    private final long baseSkillId = 104101;
    private final TargetSelector targetSelector = new RandomTargetSelector();

    public MonsterRobot_110015() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Skill baseSkill = source.getSkill(baseSkillId);
        Skill furySkill = source.getSkill(104701);
        if (furySkill != null) {
            int aliveEnemyCount = 0;
            aliveEnemyCount = allUnits.stream().filter((u) -> (!source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveEnemyCount, Integer::sum);
            if (aliveEnemyCount > 2) {
                Unit target = targetSelector.select(source, allUnits);
                if (target != null) {
                    return new UseSkillAction(furySkill, target);
                }
            }
        }
        if (baseSkill != null) {
            Unit target = targetSelector.select(source, allUnits);
            if (target != null) {
                return new UseSkillAction(baseSkill, target);
            }
        }
        return null;
    }

    @Override
    public long getId() {
        return this.id;
    }

}
