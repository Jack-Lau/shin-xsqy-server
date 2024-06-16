/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.RecoverAffect;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class RecoverSkillPerformer implements SkillPerformer {

    private boolean canRevive;

    public RecoverSkillPerformer(boolean canRevive) {
        this.canRevive = canRevive;
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return Collections.singletonList(
                new RecoverAffect(canRevive, source, skillParameterTable, skillLevel, targetCount, processCount)
        );
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        return Collections.singletonList(
                new RecoverAffect(canRevive, source, skillParameterTable, skillLevel, targetCount, processCount)
        );
    }
}
