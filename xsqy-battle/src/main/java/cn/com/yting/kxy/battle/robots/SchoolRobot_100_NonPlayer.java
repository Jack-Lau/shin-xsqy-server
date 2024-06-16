/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.DefaultTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 * 无门派的非玩家Ai
 */
public class SchoolRobot_100_NonPlayer implements Robot {

    private final long id = 109002;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_100_NonPlayer() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Unit target = targetSelector.select(source, allUnits);
        if (target != null) {
            Skill huoren = source.getSkill(100101);
            Skill xuanfengzhan = source.getSkill(100201);
            if (RandomProvider.getRandom().nextDouble() <= 0.5) {
                if (xuanfengzhan != null) {
                    if (source.getSp().getValue() >= source.getSpCost(xuanfengzhan.getCost().getSp())) {
                        return new UseSkillAction(xuanfengzhan, target);
                    }
                }
            }
            if (huoren != null) {
                return new UseSkillAction(huoren, target);
            } else {
                return new UseSkillAction(source.getSkill(100001), target);
            }
        } else {
            return null;
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

}
