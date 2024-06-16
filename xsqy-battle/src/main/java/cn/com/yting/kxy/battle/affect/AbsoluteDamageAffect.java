/*
 * Created 2018-8-18 12:11:23
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;

/**
 *
 * @author Azige
 */
public class AbsoluteDamageAffect extends AbstractDamageAffect {

    public AbsoluteDamageAffect(Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount) {
        super(true, actor, skillParameter, damageType, skillLevel, targetCount, processCount);
    }

    @Override
    protected double 攻防计算(Unit actor, Unit target, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        double finalAtk = (AffectUtils.rand(0, 1) <= (actor.getParameter(ParameterNameConstants.幸运).getValue() / 2000 * 0.05))
                ? actor.getParameter(ParameterNameConstants.物伤).getValue() * 2 : actor.getParameter(ParameterNameConstants.物伤).getValue();
        return finalAtk
                * (skillParameter.get基础系数() + skillParameter.get回数系数() * processCount)
                + skillParameter.get基础值等级系数() * skillLevel + skillParameter.get基础值常数();
    }

}
