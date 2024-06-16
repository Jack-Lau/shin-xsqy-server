/*
 * Created 2018-9-14 11:43:10
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;

/**
 * 临时使用的快速获得一些预设技能的静态类。
 *
 * @author Azige
 */
public final class Skills {

    public static final Skill ATTACK;
    public static final Skill COUNTER_ATTACK;

    public static final Skill 凝血刃;
    public static final Skill 封魂咒;

    static {
        ResourceContext context = ResourceContextHolder.getResourceContext();
        ATTACK = context.getLoader(SkillParam.class).get(100001).createSkill(1);
        COUNTER_ATTACK = context.getLoader(SkillParam.class).get(3100161).createSkill(1);
        凝血刃 = context.getLoader(SkillParam.class).get(101201).createSkill(100);
        封魂咒 = context.getLoader(SkillParam.class).get(103301).createSkill(100);
    }

    private Skills() {
    }

}
