/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;
import java.util.Objects;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.RandomTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.resource.NotResource;

/**
 * 默认 Robot，只使用特定的一种技能作为行动。
 *
 * @author Azige
 */
@NotResource
public class DefaultRobot implements Robot {

    private final long id = -1;
    private final TargetSelector targetSelector = new RandomTargetSelector();
    private final Skill skill;

    public DefaultRobot(Skill skill) {
        this.skill = Objects.requireNonNull(skill);
    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Unit target = targetSelector.select(source, allUnits);
        if (target != null) {
            return new UseSkillAction(skill, target);
        } else {
            return null;
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

}
