/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill.performers;

import cn.com.yting.kxy.battle.DamageValue;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.AttachBuffAffect;
import cn.com.yting.kxy.battle.affect.RecoverAffect;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.skill.BuffAttachSkillPerformer;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;
import cn.com.yting.kxy.core.resource.ResourceReference;
import java.util.ArrayList;

/**
 *
 * @author Darkholme
 */
public class SkillPerformer_102201 implements BuffAttachSkillPerformer {

    private ResourceReference<BuffParam> buffRef;

    @Override
    public long getId() {
        return 102201;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        List<Affect> affects = new ArrayList<>();
        double extra易疗率 = source.getParameter(ParameterNameConstants.莲华妙法强化).getValue() > 0 ? 0.1 : 0;
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
                .易伤率(skillParameterTable.get易伤率())
                .易疗率(skillParameterTable.get易疗率() + extra易疗率)
                .build();
        affects.add(new RecoverAffect(false, source, spt, skillLevel, targetCount, processCount));
        //
        if (source.getParameter(ParameterNameConstants.普陀奇穴7_甲).getValue() > 0) {
            int countDown = 3;
            SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
            parameterSpace.addParameterBase(ParameterNameConstants.再生率,
                    new SimpleParameterBase(0.02 + source.getParameter(ParameterNameConstants.普陀奇穴7_甲).getValue() * 0.004));
            //
            BuffPrototype prototype = buffRef.get().getPrototype();
            Buff buff = prototype.createBuff(source, 102201, parameterSpace, countDown, skillLevel);
            //
            SkillParameterTable sp = SkillParameterTable.builder()
                    .固定命中率(1)
                    .build();
            affects.add(new AttachBuffAffect(source, buff, sp, skillLevel, processCount));
        }
        //
        if (source.getParameter(ParameterNameConstants.普陀奇穴7_乙).getValue() > 0) {
            int countDown = 3;
            SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
            parameterSpace.addParameterBase(ParameterNameConstants.神佑率,
                    new SimpleParameterBase(0.015 + source.getParameter(ParameterNameConstants.普陀奇穴7_乙).getValue() * 0.003));
            //
            BuffPrototype prototype = ResourceContextHolder.getResourceContext().createReference(BuffParam.class, 3102014).get().getPrototype();
            Buff buff = prototype.createBuff(source, 102201, parameterSpace, countDown, skillLevel);
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
        return 3102013;
    }

    @Override
    public void setBuffReference(ResourceReference<BuffParam> ref) {
        this.buffRef = ref;
    }

}
