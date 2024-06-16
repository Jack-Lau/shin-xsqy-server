/*
 * Created 2016-5-12 15:07:38
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class DetachBuffAffect implements Affect {

    private final Unit actor;
    private final String buffName;
    private final SkillParameterTable skillParameter;
    private final int skillLevel;
    private final int processCount;

    public DetachBuffAffect(Unit actor, String buffName, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        this.actor = actor;
        this.buffName = buffName;
        this.skillParameter = skillParameter;
        this.skillLevel = skillLevel;
        this.processCount = processCount;
    }

    @Override
    public AffectRecord affect(Unit target) {
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.BUFF_DETACH;
        affectRecord.target = target;
        affectRecord.sourceId = skillParameter.getId();
        double finalHitRate = skillParameter.get固定命中率();
        if (finalHitRate == 0) {
            finalHitRate = 基础命中率计算(target);
            finalHitRate = 技能补正(finalHitRate);
            finalHitRate = 额外命中闪避补正(finalHitRate, target);
            finalHitRate = 修炼补正(finalHitRate, target);
            finalHitRate = 上下限补正(finalHitRate);
        }
        if (RandomProvider.getRandom().nextDouble() < finalHitRate) {
            Buff buff = target.removeBuff(buffName);
            if (buff != null) {
                affectRecord.buffs.add(buff);
                affectRecord.isHit = true;
                return affectRecord;
            } else {
                affectRecord.isHit = false;
                return affectRecord;
            }
        } else {
            affectRecord.isHit = false;
            return affectRecord;
        }
    }

    private double 基础命中率计算(Unit target) {
        double finalAtk = (AffectUtils.rand(0, 1) <= (actor.getParameter(ParameterNameConstants.幸运).getValue() / 2000 * 0.05))
                ? actor.getParameter(ParameterNameConstants.物伤).getValue() * 2 : actor.getParameter(ParameterNameConstants.物伤).getValue();
        return ((skillParameter.get基础系数() + processCount * skillParameter.get回数系数()) * finalAtk
                + skillParameter.get基础值等级系数() * skillLevel + skillParameter.get基础值常数())
                / (finalAtk + target.getParameter(ParameterNameConstants.物防).getValue());
    }

    private double 技能补正(double baseHitRate) {
        return baseHitRate + skillParameter.get基础命中率();
    }

    private double 额外命中闪避补正(double baseHitRate, Unit target) {
        return baseHitRate + actor.getParameter(ParameterNameConstants.额外命中率).getValue() - target.getParameter(ParameterNameConstants.额外闪避率).getValue();
    }

    private double 修炼补正(double baseHitRate, Unit target) {
        return baseHitRate + actor.getParameter(ParameterNameConstants.招式力).getValue() - target.getParameter(ParameterNameConstants.抵抗力).getValue();
    }

    private double 上下限补正(double baseHitRate) {
        return Math.min(Math.max(baseHitRate, 0.1), 0.95);
    }

}
