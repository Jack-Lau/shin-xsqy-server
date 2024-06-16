/*
 * Created 2016-3-7 10:53:38
 */
package cn.com.yting.kxy.battle.robots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.Skills;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.NotResource;

/**
 * 从一个技能集合中随机抽取技能作为行动的 Robot。
 *
 * @author Azige
 */
@NotResource
public class SkillCollectionRobot implements Robot {

    private final long id = 109001;
    private final List<Skill> skills;

    public SkillCollectionRobot(Collection<Skill> skills) {
        this.skills = new ArrayList<>(skills);
        if (!this.skills.stream().anyMatch(skillPrototype -> skillPrototype.getId() == 100001)) {
            this.skills.add(Skills.ATTACK);
        }
    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        Skill skill = skills.get(RandomProvider.getRandom().nextInt(skills.size()));
        Unit target = null;
        List<Unit> targetableUnits = allUnits.stream()
                .filter(it -> skill.checkTargetable(source, it))
                .collect(Collectors.toList());
        if (targetableUnits.size() > 0) {
            target = targetableUnits.get(RandomProvider.getRandom().nextInt(targetableUnits.size()));
        }
        if (target == null || !skill.checkAvailable(source)) {
            Unit[] rivals = allUnits.stream()
                    .filter(unit -> !source.isAlly(unit))
                    .toArray(Unit[]::new);
            if (rivals.length > 0) {
                return new UseSkillAction(source.getSkill(100001), rivals[RandomProvider.getRandom().nextInt(rivals.length)]);
            } else {
                return null;
            }
        }
        return new UseSkillAction(skill, target);
    }

    @Override
    public long getId() {
        return this.id;
    }

}
