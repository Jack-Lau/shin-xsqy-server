/*
 * Created 2018-8-18 11:59:20
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
public class MagicDamageAffect extends AbstractDamageAffect {

    public MagicDamageAffect(Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount) {
        super(false, actor, skillParameter, damageType, skillLevel, targetCount, processCount);
    }

    @Override
    protected double 攻防计算(Unit actor, Unit target, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        double finalAtk = (AffectUtils.rand(0, 1) <= (actor.getParameter(ParameterNameConstants.幸运).getValue() / 2000 * 0.05))
                ? actor.getParameter(ParameterNameConstants.法伤).getValue() * 2 : actor.getParameter(ParameterNameConstants.法伤).getValue();
        return Math.max((finalAtk + skillParameter.get额外法伤() - target.getParameter(ParameterNameConstants.法防).getValue() * (1 - skillParameter.get防御穿透率()))
                * (skillParameter.get基础系数() + skillParameter.get回数系数() * processCount), 0.05 * finalAtk)
                + skillParameter.get基础值等级系数() * skillLevel + skillParameter.get基础值常数();
    }

    @Override
    protected double 目标数补正(double value, SkillParameterTable skillParameter, int targetCount) {
        return value * Math.max((100d - skillParameter.get多目标衰减比例() * targetCount) / 100d, (100d - skillParameter.get多目标衰减比例() * skillParameter.get多目标衰减系数()) / 100d);
    }

    @Override
    protected double 波动补正(double value, Unit actor) {
        double fluctuateRate = actor.getParameter(ParameterNameConstants.法术波动下限).getValue()
                + RandomProvider.getRandom().nextDouble() * (actor.getParameter(ParameterNameConstants.法术波动上限).getValue() - actor.getParameter(ParameterNameConstants.法术波动下限).getValue());
        return value * fluctuateRate;
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
