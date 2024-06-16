/*
 * Created 2018-8-17 18:41:37
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class PhysicsDamageAffect extends AbstractDamageAffect {

    public PhysicsDamageAffect(Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount) {
        super(true, actor, skillParameter, damageType, skillLevel, targetCount, processCount);
    }

    public PhysicsDamageAffect(Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount, boolean isMainTarget) {
        super(true, actor, skillParameter, damageType, skillLevel, targetCount, processCount, isMainTarget);
    }

    public PhysicsDamageAffect(boolean blockable, Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount) {
        super(blockable, actor, skillParameter, damageType, skillLevel, targetCount, processCount);
    }

    public PhysicsDamageAffect(boolean blockable, Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount, boolean isMainTarget) {
        super(blockable, actor, skillParameter, damageType, skillLevel, targetCount, processCount, isMainTarget);
    }

    @Override
    protected double 攻防计算(Unit actor, Unit target, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        double finalAtk = (AffectUtils.rand(0, 1) <= (actor.getParameter(ParameterNameConstants.幸运).getValue() / 2000 * 0.05))
                ? actor.getParameter(ParameterNameConstants.物伤).getValue() * 2 : actor.getParameter(ParameterNameConstants.物伤).getValue();
        return Math.max((finalAtk + skillParameter.get额外物伤() - target.getParameter(ParameterNameConstants.物防).getValue() * (1 - skillParameter.get防御穿透率()))
                * (skillParameter.get基础系数() + skillParameter.get回数系数() * processCount), 0.05 * finalAtk)
                + skillParameter.get基础值等级系数() * skillLevel + skillParameter.get基础值常数();
    }

    @Override
    protected double 吸收补正(double value, Unit target, SkillParameterTable skillParameter) {
        double absorbRate = 0;
        switch (skillParameter.get元素类型()) {
            case 风:
                absorbRate = target.getParameter(ParameterNameConstants.风属性吸收率).getValue();
                break;
            case 雷:
                absorbRate = target.getParameter(ParameterNameConstants.雷属性吸收率).getValue();
                break;
            case 火:
                absorbRate = target.getParameter(ParameterNameConstants.火属性吸收率).getValue();
                break;
            case 土:
                absorbRate = target.getParameter(ParameterNameConstants.土属性吸收率).getValue();
                break;
            case 无:
                absorbRate = target.getParameter(ParameterNameConstants.无属性吸收率).getValue();
                break;
            default:
                absorbRate = 0;
                break;
        }
        if (RandomProvider.getRandom().nextDouble() < absorbRate) {
            return -value;
        } else {
            return value;
        }
    }

}
