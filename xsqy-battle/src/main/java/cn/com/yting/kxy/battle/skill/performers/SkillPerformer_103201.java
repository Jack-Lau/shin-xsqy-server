/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill.performers;

import cn.com.yting.kxy.battle.DamageValue;
import java.util.ArrayList;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.AttachBuffAffect;
import cn.com.yting.kxy.battle.affect.DetachBuffAffect;
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
public class SkillPerformer_103201 implements BuffAttachSkillPerformer {

    private ResourceReference<BuffParam> buffRef;

    @Override
    public long getId() {
        return 103201;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        List<Affect> affects = new ArrayList<>();
        int countDown = 2;
        SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
        parameterSpace.addParameterBase(ParameterNameConstants.免疫封印, new SimpleParameterBase(1));
        if (source.getParameter(ParameterNameConstants.六脉血逆强化).getValue() > 0) {
            double source物伤 = source.getParameter(ParameterNameConstants.物伤).getValue();
            parameterSpace.addParameterBase(ParameterNameConstants.物防, new SimpleParameterBase(source物伤 * 0.03 + skillLevel + 20));
            parameterSpace.addParameterBase(ParameterNameConstants.法防, new SimpleParameterBase(source物伤 * 0.03 + skillLevel + 20));
        }
        //
        BuffPrototype prototype = buffRef.get().getPrototype();
        Buff buff = prototype.createBuff(source, 103201, parameterSpace, countDown, skillLevel);
        affects.add(new DetachBuffAffect(source, "万蛊噬心", skillParameterTable, skillLevel, processCount));
        affects.add(new DetachBuffAffect(source, "封魂咒", skillParameterTable, skillLevel, processCount));
        affects.add(new AttachBuffAffect(source, buff, skillParameterTable, skillLevel, processCount));
        return affects;
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return processMainTarget(source, target, processCount, skillParameterTable, skillLevel, targetCount, cost);
    }

    @Override
    public long getBuffId() {
        return 3102001;
    }

    @Override
    public void setBuffReference(ResourceReference<BuffParam> ref) {
        this.buffRef = ref;
    }

}
