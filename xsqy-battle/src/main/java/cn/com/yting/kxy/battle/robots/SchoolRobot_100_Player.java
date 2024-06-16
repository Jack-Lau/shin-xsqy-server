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

/**
 * 无门派的玩家Ai
 */
public class SchoolRobot_100_Player implements Robot {

    private final long id = 109003;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_100_Player() {

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
