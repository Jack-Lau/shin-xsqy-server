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
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.skill.BuffAttachSkillPerformer;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;
import cn.com.yting.kxy.core.resource.ResourceReference;
import java.util.ArrayList;

/**
 *
 * @author Darkholme
 */
public class SkillPerformer_103501 implements BuffAttachSkillPerformer {

    private ResourceReference<BuffParam> buffRef;

    @Override
    public long getId() {
        return 103501;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        int countDown = 3;
        if (source.getParameter(ParameterNameConstants.盘丝奇穴6_甲).getValue() > 0) {
            countDown += RandomProvider.getRandom().nextDouble() < (0.3 + source.getParameter(ParameterNameConstants.盘丝奇穴6_甲).getValue() * 0.035) ? 1 : 0;
        }
        SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
        parameterSpace.addParameterBase(ParameterNameConstants.物伤, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.08 + skillLevel * 2.5 + 170));
        parameterSpace.addParameterBase(ParameterNameConstants.法伤, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.08 + skillLevel * 2.5 + 170));
        parameterSpace.addParameterBase(ParameterNameConstants.物防, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.05 + skillLevel * 1.6 + 130));
        parameterSpace.addParameterBase(ParameterNameConstants.法防, new SimpleParameterBase(source.getParameter(ParameterNameConstants.物伤).getValue() * 0.05 + skillLevel * 1.6 + 130));
        if (source.getParameter(ParameterNameConstants.盘丝奇穴6_乙).getValue() > 0) {
            parameterSpace.addParameterBase(ParameterNameConstants.抵抗力, new SimpleParameterBase(0.02 + source.getParameter(ParameterNameConstants.盘丝奇穴6_乙).getValue() * 0.002));
        }
        //
        List<Affect> affects = new ArrayList<>();
        BuffPrototype prototype = buffRef.get().getPrototype();
        Buff buff = prototype.createBuff(source, 103501, parameterSpace, countDown, skillLevel);
        affects.add(new AttachBuffAffect(source, buff, skillParameterTable, skillLevel, processCount));
        if (source.getParameter(ParameterNameConstants.七煞诀强化).getValue() > 0) {
            SimpleParameterSpace extraParameterSpace = new SimpleParameterSpace();
            extraParameterSpace.addParameterBase(ParameterNameConstants.易伤率, new SimpleParameterBase(-0.16));
            Buff extraBuff = ResourceContextHolder.getResourceContext().createReference(BuffParam.class, 3102005).get().getPrototype().createBuff(source, 103501, extraParameterSpace, countDown, skillLevel);
            affects.add(new AttachBuffAffect(source, extraBuff, skillParameterTable, skillLevel, processCount));
        }
        return affects;
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return processMainTarget(source, target, processCount, skillParameterTable, skillLevel, targetCount, cost);
    }

    @Override
    public long getBuffId() {
        return 3102003;
    }

    @Override
    public void setBuffReference(ResourceReference<BuffParam> ref) {
        this.buffRef = ref;
    }

}
