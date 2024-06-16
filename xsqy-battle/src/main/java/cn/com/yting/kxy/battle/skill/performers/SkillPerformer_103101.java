/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill.performers;

import cn.com.yting.kxy.battle.DamageValue;
import java.util.Collections;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.AttachBuffAffect;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.skill.BuffAttachSkillPerformer;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceReference;

/**
 *
 * @author Darkholme
 */
public class SkillPerformer_103101 implements BuffAttachSkillPerformer {

    private ResourceReference<BuffParam> buffRef;

    @Override
    public long getId() {
        return 103101;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        int countDown = 2;
        SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
        parameterSpace.addParameterBase(ParameterNameConstants.无法使用固有技, new SimpleParameterBase(1));
        double extra流血值 = source.getParameter(ParameterNameConstants.万蛊噬心强化).getValue() > 0 ? source.getParameter(ParameterNameConstants.物伤).getValue() * 0.04 : 0;
        parameterSpace.addParameterBase(ParameterNameConstants.流血值, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.3
                + skillLevel * 1 + 300
                + extra流血值));
        if (source.getParameter(ParameterNameConstants.盘丝奇穴8_甲).getValue() > 0) {
            parameterSpace.addParameterBase(ParameterNameConstants.怒气消耗率, new SimpleParameterBase(0.05 + source.getParameter(ParameterNameConstants.盘丝奇穴8_甲).getValue() * 0.005));
        }
        if (source.getParameter(ParameterNameConstants.盘丝奇穴8_乙).getValue() > 0) {
            parameterSpace.addParameterBase(ParameterNameConstants.生命回复率, new SimpleParameterBase(-0.12 + source.getParameter(ParameterNameConstants.盘丝奇穴8_乙).getValue() * -0.014));
        }
        //
        BuffPrototype prototype = buffRef.get().getPrototype();
        Buff buff = prototype.createBuff(source, 103101, parameterSpace, countDown, skillLevel);
        return Collections.singletonList(
                new AttachBuffAffect(source, buff, skillParameterTable, skillLevel, processCount)
        );
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return processMainTarget(source, target, processCount, skillParameterTable, skillLevel, targetCount, cost);
    }

    @Override
    public long getBuffId() {
        return 3102000;
    }

    @Override
    public void setBuffReference(ResourceReference<BuffParam> ref) {
        this.buffRef = ref;
    }

}
