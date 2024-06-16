/*
 * Created 2018-8-18 17:59:13
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.DamageValue;
import java.util.Collections;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.AbsoluteDamageAffect;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect.DamageType;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.affect.MagicDamageAffect;
import cn.com.yting.kxy.battle.affect.PhysicsDamageAffect;

/**
 *
 * @author Azige
 */
public class DamageSkillPerformer implements SkillPerformer {

    private DamageType damageType = DamageType.PHYSICS;

    public DamageSkillPerformer(String templateParam) {
        switch (templateParam) {
            case "物理":
                this.damageType = DamageType.PHYSICS;
                break;
            case "法术":
                this.damageType = DamageType.MAGIC;
                break;
            case "真实":
                this.damageType = DamageType.ABSOLUTE;
                break;
            default:
                this.damageType = DamageType.PHYSICS;
        }
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        switch (damageType) {
            case PHYSICS:
                return Collections.singletonList(
                        new PhysicsDamageAffect(source, skillParameterTable, DamageType.PHYSICS, skillLevel, targetCount, processCount)
                );
            case MAGIC:
                return Collections.singletonList(
                        new MagicDamageAffect(source, skillParameterTable, DamageType.MAGIC, skillLevel, targetCount, processCount)
                );
            case ABSOLUTE:
                return Collections.singletonList(
                        new AbsoluteDamageAffect(source, skillParameterTable, DamageType.ABSOLUTE, skillLevel, targetCount, processCount)
                );
            default:
                return Collections.singletonList(
                        new PhysicsDamageAffect(source, skillParameterTable, DamageType.PHYSICS, skillLevel, targetCount, processCount)
                );
        }
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, SkillParameterTable skillParameterTable, int skillLevel, int targetCount, DamageValue cost) {
        switch (damageType) {
            case PHYSICS:
                return Collections.singletonList(
                        new PhysicsDamageAffect(source, skillParameterTable, DamageType.PHYSICS, skillLevel, targetCount, processCount)
                );
            case MAGIC:
                return Collections.singletonList(
                        new MagicDamageAffect(source, skillParameterTable, DamageType.MAGIC, skillLevel, targetCount, processCount)
                );
            case ABSOLUTE:
                return Collections.singletonList(
                        new AbsoluteDamageAffect(source, skillParameterTable, DamageType.ABSOLUTE, skillLevel, targetCount, processCount)
                );
            default:
                return Collections.singletonList(
                        new PhysicsDamageAffect(source, skillParameterTable, DamageType.PHYSICS, skillLevel, targetCount, processCount)
                );
        }
    }
}
