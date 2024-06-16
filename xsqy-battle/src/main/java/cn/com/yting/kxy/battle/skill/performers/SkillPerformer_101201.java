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
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.PhysicsDamageAffect;
import cn.com.yting.kxy.battle.skill.ResourceSkillPerformer;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;

/**
 *
 * @author Darkholme
 */
public class SkillPerformer_101201 implements ResourceSkillPerformer {

    @Override
    public long getId() {
        return 101201;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        double lostHpPercent = 1 - target.getHp().getRate();
        double extra基础值常数 = source.getParameter(ParameterNameConstants.凝血刃强化).getValue() > 0 ? 4 * skillLevel + 150 : 0;
        SkillParameterTable spt = SkillParameterTable.builder()
                .id(skillParameterTable.getId())
                .元素类型(skillParameterTable.get元素类型())
                .固定命中率(skillParameterTable.get固定命中率())
                .基础命中率(skillParameterTable.get基础命中率())
                .基础系数(skillParameterTable.get基础系数())
                .回数系数(skillParameterTable.get回数系数())
                .基础值等级系数((skillParameterTable.get基础值等级系数()) * lostHpPercent)
                .基础值常数((skillParameterTable.get基础值常数() + extra基础值常数) * lostHpPercent)
                .多目标衰减系数(skillParameterTable.get多目标衰减系数())
                .多目标衰减比例(skillParameterTable.get多目标衰减比例())
                .防御穿透率(skillParameterTable.get防御穿透率())
                .额外暴击率(skillParameterTable.get额外暴击率())
                .易伤率(skillParameterTable.get易伤率())
                .build();
        boolean blockable = source.getParameter(ParameterNameConstants.凌霄奇穴5_乙).getValue() <= 0;
        return Collections.singletonList(
                new PhysicsDamageAffect(blockable, source, spt, AbstractDamageAffect.DamageType.PHYSICS, skillLevel, targetCount, processCount)
        );
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return processMainTarget(source, target, processCount, skillParameterTable, skillLevel, targetCount, cost);
    }

}
