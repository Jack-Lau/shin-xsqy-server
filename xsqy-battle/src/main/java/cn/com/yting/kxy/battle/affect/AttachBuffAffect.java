/*
 * Created 2015-10-21 15:34:31
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class AttachBuffAffect implements Affect {

    private final Unit actor;
    private final Buff buff;
    private final SkillParameterTable skillParameter;
    private final int skillLevel;
    private final int processCount;

    public AttachBuffAffect(Unit actor, Buff buff, SkillParameterTable skillParameter, int skillLevel, int processCount) {
        this.actor = actor;
        this.buff = buff;
        this.skillParameter = skillParameter;
        this.skillLevel = skillLevel;
        this.processCount = processCount;
    }

    public Buff getBuff() {
        return buff;
    }

    @Override
    public AffectRecord affect(Unit target) {
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.BUFF_ATTACH;
        affectRecord.target = target;
        affectRecord.sourceId = buff.getSourceId();
        affectRecord.buffs.add(buff);
        affectRecord.isHit = false;
        boolean resisted = false;
        if (buff.getPrototype().getType() == BuffPrototype.Type.控制) {
            if (target.getParameter(ParameterNameConstants.免疫封印).getValue() > 0) {
                resisted = true;
            }
            if (buff.getId() == 3102000
                    && actor.getParameter(ParameterNameConstants.盘丝奇穴7_乙).getValue() > 0
                    && RandomProvider.getRandom().nextDouble() < (0.3 + actor.getParameter(ParameterNameConstants.盘丝奇穴7_乙).getValue() * 0.035)) {
                resisted = false;
            }
        }
        if (!resisted) {
            double finalHitRate = skillParameter.get固定命中率();
            if (finalHitRate == 0) {
                finalHitRate = 基础命中率计算(target);
                finalHitRate = 技能补正(finalHitRate);
                finalHitRate = 额外命中闪避补正(finalHitRate, target);
                finalHitRate = 修炼补正(finalHitRate, target);
                finalHitRate = 上下限补正(finalHitRate);
            }
            if (RandomProvider.getRandom().nextDouble() < finalHitRate) {
                boolean addBuff = target.addBuff(buff);
                if (addBuff) {
                    affectRecord.isHit = true;
                }
            }
        }
        return affectRecord;
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
