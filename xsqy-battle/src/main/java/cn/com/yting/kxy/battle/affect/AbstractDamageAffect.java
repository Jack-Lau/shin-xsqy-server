/*
 * Created 2018-8-17 18:56:37
 */
package cn.com.yting.kxy.battle.affect;

import java.util.Random;

import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public abstract class AbstractDamageAffect implements Affect {

    public enum DamageType {
        PHYSICS,
        MAGIC,
        ABSOLUTE
    }

    private boolean blockable;
    private boolean isMainTarget;

    // TODO: 重构为 Actor 类型
    private Unit actor;
    private SkillParameterTable skillParameter;
    private DamageType damageType;
    private int skillLevel;
    private int targetCount;
    private int processCount;

    public AbstractDamageAffect(boolean blockable, Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount) {
        this.blockable = blockable;
        this.actor = actor;
        this.skillParameter = skillParameter;
        this.damageType = damageType;
        this.skillLevel = skillLevel;
        this.targetCount = targetCount;
        this.processCount = processCount;
        this.isMainTarget = true;
    }

    public AbstractDamageAffect(boolean blockable, Unit actor, SkillParameterTable skillParameter, DamageType damageType, int skillLevel, int targetCount, int processCount, boolean isMainTarget) {
        this.blockable = blockable;
        this.actor = actor;
        this.skillParameter = skillParameter;
        this.damageType = damageType;
        this.skillLevel = skillLevel;
        this.targetCount = targetCount;
        this.processCount = processCount;
        this.isMainTarget = isMainTarget;
    }

    @Override
    public AffectRecord affect(Unit target) {
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.DAMAGE;
        affectRecord.damageType = damageType;
        affectRecord.actor = actor;
        affectRecord.sourceId = skillParameter.getId();
        affectRecord.target = target;
        affectRecord.isMainTarget = isMainTarget;
        if (target.isHpZero()) {
            affectRecord.isOverKill = true;
        }
        double finalHitRate = skillParameter.get固定命中率();
        if (finalHitRate == 0) {
            finalHitRate = skillParameter.get基础命中率()
                    + actor.getParameter(ParameterNameConstants.额外命中率).getValue() - target.getParameter(ParameterNameConstants.额外闪避率).getValue();
            finalHitRate = Math.min(finalHitRate, 1.0);
            finalHitRate = Math.max(0.1, finalHitRate);
        }
        Random random = RandomProvider.getRandom();
        if (random.nextDouble() < finalHitRate) {
            boolean criticalFlag = false;
            boolean blockedFlag = false;
            boolean absorbFlag = false;
            double value;
            value = 攻防计算(actor, target, skillParameter, skillLevel, processCount);
            value = 随机补正(value);
            value = 修炼补正(value, actor, target);
            // 处理霸体
            if (actor.getParameter(ParameterNameConstants.霸体_伤害).getValue() > 0 && damageType == DamageType.PHYSICS) {
                value += actor.getParameter(ParameterNameConstants.最大生命).getValue() * actor.getParameter(ParameterNameConstants.霸体_伤害).getValue();
            }
            // 处理暴击
            if (random.nextDouble() < actor.getParameter(ParameterNameConstants.暴击率).getValue() + skillParameter.get额外暴击率()) {
                value = 暴击补正(value, actor, skillParameter);
                criticalFlag = true;
            }
            // 处理格挡
            if ((blockable || target.getParameter(ParameterNameConstants.普陀奇穴8_乙).getValue() > 0)
                    && random.nextDouble() < target.getParameter(ParameterNameConstants.格挡率).getValue()) {
                value = 格挡补正(value, target);
                blockedFlag = true;
            }
            value = 目标类型补正(value, actor, target);
            value = 目标数补正(value, skillParameter, targetCount);
            value = 波动补正(value, actor);
            value = 易伤补正(value, target, skillParameter);
            value = 怒火补正(value, target);
            // 处理吸收
            value = 吸收补正(value, target, skillParameter);
            if (value < 0) {
                affectRecord.type = AffectRecord.AffectRecordType.RECOVER;
                value = -value;
                absorbFlag = true;
            }
            DamageValue finalDamageValue = DamageValue.hpOnly(value);
            finalDamageValue = finalDamageValue.normalize();
            if (affectRecord.type == AffectRecord.AffectRecordType.DAMAGE) {
                target.takeDamage(finalDamageValue);
            } else {
                target.takeRecover(finalDamageValue);
            }
            //
            affectRecord.value.hp = (long) finalDamageValue.getHp();
            affectRecord.value.sp = (long) finalDamageValue.getSp();
            affectRecord.isHit = true;
            affectRecord.isCritical = criticalFlag;
            affectRecord.isBlock = blockedFlag;
            affectRecord.isAbsorb = absorbFlag;
            return affectRecord;
        } else {
            affectRecord.isHit = false;
            return affectRecord;
        }
    }

    protected abstract double 攻防计算(Unit actor, Unit target, SkillParameterTable skillParameter, int skillLevel, int processCount);

    protected double 随机补正(double value) {
        return AffectUtils.rand(value * 0.95, value * 1.05);
    }

    protected double 修炼补正(double value, Unit actor, Unit target) {
        return value * (1 + actor.getParameter(ParameterNameConstants.招式力).getValue() - target.getParameter(ParameterNameConstants.抵抗力).getValue());
    }

    protected double 暴击补正(double value, Unit actor, SkillParameterTable skillParameter) {
        return value * (1 + (actor.getParameter(ParameterNameConstants.暴击效果).getValue() + skillParameter.get额外暴击效果() - 1));
    }

    protected double 格挡补正(double value, Unit target) {
        double 格挡伤害减免 = 0.5;
        if (target.getParameter(ParameterNameConstants.普陀奇穴8_乙).getValue() > 0) {
            格挡伤害减免 += target.getParameter(ParameterNameConstants.普陀奇穴8_乙).getValue() * 0.0075;
        }
        return value * (1 - 格挡伤害减免);
    }

    protected double 目标类型补正(double value, Unit actor, Unit target) {
        if (actor.getParameter(ParameterNameConstants.五庄奇穴8_乙).getValue() > 0
                && target.getType() != Unit.UnitType.TYPE_PLAYER) {
            return value * (1 + (0.025 + actor.getParameter(ParameterNameConstants.五庄奇穴8_乙).getValue() * 0.00375));
        }
        return value;
    }

    protected double 目标数补正(double value, SkillParameterTable skillParameter, int tagetCount) {
        return value;
    }

    protected double 波动补正(double value, Unit actor) {
        return value;
    }

    protected double 易伤补正(double value, Unit target, SkillParameterTable skillParameter) {
        return Math.max(value * (1 + target.getParameter(ParameterNameConstants.易伤率).getValue() + skillParameter.get易伤率()), 1);
    }

    protected double 怒火补正(double value, Unit target) {
        return value * (1 + target.get怒火补正率());
    }

    protected double 吸收补正(double value, Unit target, SkillParameterTable skillParameter) {
        return value;
    }

}
