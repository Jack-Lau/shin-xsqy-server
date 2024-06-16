/*
 * Created 2015-10-29 17:13:31
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;
import java.util.Random;

/**
 *
 * @author Azige
 */
public class RecoverAffect implements Affect {

    private boolean canRevive;

    private Unit actor;
    private SkillParameterTable skillParameter;
    private int skillLevel;
    private int targetCount;
    private int processCount;

    public RecoverAffect(boolean canRevive, Unit actor, SkillParameterTable skillParameter, int skillLevel, int targetCount, int processCount) {
        this.canRevive = canRevive;
        this.actor = actor;
        this.skillParameter = skillParameter;
        this.skillLevel = skillLevel;
        this.targetCount = targetCount;
        this.processCount = processCount;
    }

    @Override
    public AffectRecord affect(Unit target) {
        AffectRecord affectRecord = new AffectRecord();
        if (target.isHpZero() && !target.isFlyable() && canRevive) {
            affectRecord.type = AffectRecord.AffectRecordType.REVIVE;
            target.setDead(false);
        } else {
            affectRecord.type = AffectRecord.AffectRecordType.RECOVER;
        }
        affectRecord.actor = actor;
        affectRecord.sourceId = skillParameter.getId();
        affectRecord.target = target;
        Random random = RandomProvider.getRandom();
        boolean criticalFlag = false;
        double value;
        value = 基础计算(actor, skillParameter, skillLevel, processCount);
        value = 随机补正(value);
        value = 修炼补正(value, actor, target);
        // 处理暴击
        if (random.nextDouble() < actor.getParameter(ParameterNameConstants.暴击率).getValue()) {
            value = 暴击补正(value, actor);
            criticalFlag = true;
        }
        value = 目标数补正(value, skillParameter, targetCount);
        value = 易疗补正(value, target, skillParameter);
        value = 生命回复补正(value, target);
        DamageValue finalDamageValue = DamageValue.hpOnly(value);
        // 取整
        finalDamageValue = finalDamageValue.normalize();
        target.takeRecover(finalDamageValue);
        //
        affectRecord.value.hp = (long) finalDamageValue.getHp();
        affectRecord.value.sp = (long) finalDamageValue.getSp();
        affectRecord.isHit = true;
        affectRecord.isCritical = criticalFlag;
        return affectRecord;
    }

    protected double 基础计算(Unit actor, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        double finalAtk = (AffectUtils.rand(0, 1) <= (actor.getParameter(ParameterNameConstants.幸运).getValue() / 2000 * 0.05))
                ? actor.getParameter(ParameterNameConstants.法伤).getValue() * 2 : actor.getParameter(ParameterNameConstants.法伤).getValue();
        return (finalAtk + skillParameter.get额外法伤()) * (skillParameter.get基础系数() + skillParameter.get回数系数() * processCount)
                + skillParameter.get基础值等级系数() * skillLevel + skillParameter.get基础值常数();
    }

    protected double 随机补正(double value) {
        return AffectUtils.rand(value * 0.95, value * 1.05);
    }

    protected double 修炼补正(double value, Unit actor, Unit target) {
        return value * (1 + actor.getParameter(ParameterNameConstants.招式力).getValue());
    }

    protected double 暴击补正(double value, Unit actor) {
        return value * (1 + (actor.getParameter(ParameterNameConstants.暴击效果).getValue() - 1));
    }

    protected double 目标数补正(double value, SkillParameterTable skillParameter, int tagetCount) {
        return value * Math.max((100d - skillParameter.get多目标衰减比例() * targetCount) / 100d, (100d - skillParameter.get多目标衰减比例() * skillParameter.get多目标衰减系数()) / 100d);
    }

    protected double 易疗补正(double value, Unit actor, SkillParameterTable skillParameter) {
        return value * (1 + actor.getParameter(ParameterNameConstants.易疗率).getValue() + skillParameter.get易疗率());
    }

    protected double 生命回复补正(double value, Unit target) {
        return Math.max(value * target.getParameter(ParameterNameConstants.生命回复率).getValue(), 1);
    }

}
