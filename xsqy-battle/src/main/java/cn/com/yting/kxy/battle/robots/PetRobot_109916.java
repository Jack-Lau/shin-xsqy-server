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
 * 宠物Ai
 */
public class PetRobot_109916 implements Robot {

    private final long id = 109916;
    private final long baseSkillId = 3101250;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public PetRobot_109916() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Skill baseSkill = source.getSkill(baseSkillId);
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
