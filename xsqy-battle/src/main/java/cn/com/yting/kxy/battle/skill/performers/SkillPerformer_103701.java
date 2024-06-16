/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill.performers;

import cn.com.yting.kxy.battle.DamageValue;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.AttachBuffAffect;
import cn.com.yting.kxy.battle.affect.PhysicsDamageAffect;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.skill.BuffAttachSkillPerformer;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceReference;
import java.util.ArrayList;

/**
 *
 * @author Darkholme
 */
public class SkillPerformer_103701 implements BuffAttachSkillPerformer {

    private ResourceReference<BuffParam> buffRef;

    @Override
    public long getId() {
        return 103701;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        List<Affect> affects = new ArrayList<>();
        double extra易伤率 = source.getParameter(ParameterNameConstants.穿花箭强化).getValue() > 0 ? 0.08 : 0;
        double extra物伤 = source.getParameter(ParameterNameConstants.盘丝奇穴3_乙).getValue() > 0
                ? source.getParameter(ParameterNameConstants.速度).getValue() * (0.25 + source.getParameter(ParameterNameConstants.盘丝奇穴3_乙).getValue() * 0.025) : 0;
        SkillParameterTable spt = SkillParameterTable.builder()
                .id(skillParameterTable.getId())
                .元素类型(skillParameterTable.get元素类型())
                .固定命中率(skillParameterTable.get固定命中率())
                .基础命中率(skillParameterTable.get基础命中率())
                .基础系数(skillParameterTable.get基础系数())
                .回数系数(skillParameterTable.get回数系数())
                .基础值等级系数(skillParameterTable.get基础值等级系数())
                .基础值常数(skillParameterTable.get基础值常数())
                .多目标衰减系数(skillParameterTable.get多目标衰减系数())
                .多目标衰减比例(skillParameterTable.get多目标衰减比例())
                .防御穿透率(skillParameterTable.get防御穿透率())
                .额外暴击率(skillParameterTable.get额外暴击率())
                .易伤率(skillParameterTable.get易伤率() + extra易伤率)
                .额外物伤(skillParameterTable.get额外物伤() + extra物伤)
                .build();
        affects.add(new PhysicsDamageAffect(source, spt, AbstractDamageAffect.DamageType.PHYSICS, skillLevel, targetCount, processCount));
        //
        if (source.getParameter(ParameterNameConstants.盘丝奇穴3_甲).getValue() > 0
                && RandomProvider.getRandom().nextDouble() < (0.04 + source.getParameter(ParameterNameConstants.盘丝奇穴3_甲).getValue() * 0.004)) {
            int countDown = 1;
            SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
            parameterSpace.addParameterBase(ParameterNameConstants.无法使用固有技, new SimpleParameterBase(1));
            parameterSpace.addParameterBase(ParameterNameConstants.流血值, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.3
                    + skillLevel * 1 + 300));
            if (source.getParameter(ParameterNameConstants.盘丝奇穴8_甲).getValue() > 0) {
                parameterSpace.addParameterBase(ParameterNameConstants.怒气消耗率, new SimpleParameterBase(0.05 + source.getParameter(ParameterNameConstants.盘丝奇穴8_甲).getValue() * 0.005));
            }
            if (source.getParameter(ParameterNameConstants.盘丝奇穴8_乙).getValue() > 0) {
                parameterSpace.addParameterBase(ParameterNameConstants.生命回复率, new SimpleParameterBase(-0.12 + source.getParameter(ParameterNameConstants.盘丝奇穴8_乙).getValue() * -0.014));
            }
            //
            BuffPrototype prototype = buffRef.get().getPrototype();
            Buff buff = prototype.createBuff(source, 103701, parameterSpace, countDown, skillLevel);
            //
            SkillParameterTable sp = SkillParameterTable.builder()
                    .固定命中率(1)
                    .build();
            affects.add(new AttachBuffAffect(source, buff, sp, skillLevel, processCount));
        }
        return affects;
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return processMainTarget(source, target, processCount, skillParameterTable, skillLevel, targetCount, cost);
    }

    @Override
    public long getBuffId() {
        return 3102002;
    }

    @Override
    public void setBuffReference(ResourceReference<BuffParam> ref) {
        this.buffRef = ref;
    }

}
