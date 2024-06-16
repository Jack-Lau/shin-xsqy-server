/*
 * Created 2018-8-18 16:32:37
 */
package cn.com.yting.kxy.battle.skill;

import java.util.List;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.battle.skill.resource.SkillParam.ElementType;
import cn.com.yting.kxy.battle.skill.resource.SkillParam.SkillType;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 * 使用技能参数和技能行为合成一个技能
 *
 * @author Azige
 */
public class ChimeraSkill implements Skill {

    private int level;

    private final SkillParam skillParam;
    private final SkillParameterTable skillParameterTable;
    private final SkillPerformer skillPerformer;

    public ChimeraSkill(SkillParam skillParam, SkillPerformer skillPerformer) {
        this.skillParam = skillParam;
        this.skillParameterTable = skillParam.toSkillParameterTable();
        this.skillPerformer = skillPerformer;
    }

    @Override
    public long getId() {
        return skillParam.getId();
    }

    @Override
    public String getName() {
        return skillParam.getName();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public SkillType getType() {
        return skillParam.getSkillType();
    }

    @Override
    public ElementType getElementType() {
        return skillParam.getElementType();
    }

    @Override
    public int getTargetType() {
        return skillParam.getTargetType();
    }

    @Override
    public int getMaxTargetCount(Unit source) {
        int id = (int) skillParam.getId();
        int finalMaxTargetCount = skillParam.getMaxTargetCount();
        switch (id) {
            case 101101: {
                finalMaxTargetCount -= source.getParameter(ParameterNameConstants.凌霄奇穴6_乙).getValue() > 0 ? 1 : 0;
                break;
            }
            case 101301: {
                if (source.getParameter(ParameterNameConstants.凌霄奇穴3_甲).getValue() > 0) {
                    finalMaxTargetCount += RandomProvider.getRandom().nextDouble() < (0.1 + source.getParameter(ParameterNameConstants.凌霄奇穴3_甲).getValue() * 0.01) ? 1 : 0;
                }
                break;
            }
            case 104201: {
                if (source.getParameter(ParameterNameConstants.五庄奇穴3_甲).getValue() > 0) {
                    finalMaxTargetCount += RandomProvider.getRandom().nextDouble() < (0.25 + source.getParameter(ParameterNameConstants.五庄奇穴3_甲).getValue() * 0.0375) ? 1 : 0;
                }
                break;
            }
            case 104501: {
                if (source.getParameter(ParameterNameConstants.五庄奇穴4_甲).getValue() > 0) {
                    finalMaxTargetCount += RandomProvider.getRandom().nextDouble() < (0.1 + source.getParameter(ParameterNameConstants.五庄奇穴4_甲).getValue() * 0.01) ? 1 : 0;
                }
                break;
            }
            case 102301: {
                if (source.getParameter(ParameterNameConstants.普陀奇穴4_甲).getValue() > 0) {
                    finalMaxTargetCount += RandomProvider.getRandom().nextDouble() < (0.15 + source.getParameter(ParameterNameConstants.普陀奇穴4_甲).getValue() * 0.025) ? 1 : 0;
                }
                break;
            }
            case 103101: {
                if (source.getParameter(ParameterNameConstants.盘丝奇穴7_甲).getValue() > 0) {
                    finalMaxTargetCount += RandomProvider.getRandom().nextDouble() < (0.15 + source.getParameter(ParameterNameConstants.盘丝奇穴7_甲).getValue() * 0.0175) ? 1 : 0;
                }
                break;
            }
            case 3101240: {
                finalMaxTargetCount = 2 + RandomProvider.getRandom().nextInt(4);
                break;
            }
            case 3101270: {
                finalMaxTargetCount = 3 + RandomProvider.getRandom().nextInt(4);
                break;
            }
            default:
                break;
        }
        return finalMaxTargetCount;
    }

    @Override
    public int getMaxProcessCount(Unit source) {
        int id = (int) skillParam.getId();
        int finalMaxProcessCount = skillParam.getMaxAffectCount();
        switch (id) {
            case 102701:
                finalMaxProcessCount += source.getParameter(ParameterNameConstants.大慈心光渡强化).getValue() > 0 ? 1 : 0;
                break;
            case 102702:
                finalMaxProcessCount += source.getParameter(ParameterNameConstants.大慈心光渡强化).getValue() > 0 ? 1 : 0;
                break;
            case 101501:
                if (source.getParameter(ParameterNameConstants.凌霄奇穴4_乙).getValue() > 0) {
                    finalMaxProcessCount += RandomProvider.getRandom().nextDouble() < (0.12 + source.getParameter(ParameterNameConstants.凌霄奇穴4_乙).getValue() * 0.014) ? 1 : 0;
                }
                break;
            default:
                break;
        }
        return finalMaxProcessCount;
    }

    @Override
    public double getExtraMultihitRate(Unit source) {
        int id = (int) skillParam.getId();
        double finalExtraMultihitRate = 0;
        if (id == 104201) {
            finalExtraMultihitRate += source.getParameter(ParameterNameConstants.剑雨藏锋强化).getValue() > 0 ? 0.05 : 0;
        } else if (id == 104601) {
            if (source.getParameter(ParameterNameConstants.五庄奇穴5_甲).getValue() > 0) {
                finalExtraMultihitRate += 0.05 + source.getParameter(ParameterNameConstants.五庄奇穴5_甲).getValue() * 0.01;
            }
        }
        return finalExtraMultihitRate;
    }

    @Override
    public double getPriority() {
        return 0;
    }

    @Override
    public DamageValue getCost() {
        double cost = 0;
        if (skillParam.getCost() != null) {
            cost = Double.parseDouble(skillParam.getCost());
        }
        return DamageValue.spOnly(cost);
    }

    @Override
    public boolean canMultihit() {
        return skillParam.canMultiHit();
    }

    @Override
    public boolean checkAvailable(Unit source) {
        return true;
    }

    @Override
    public boolean checkTargetable(Unit source, Unit target) {
        return skillParam.checkTargetTable(source, target);
    }

    @Override
    public void onUnitAttending(BattleDirector battleDirector, Unit unit) {
    }

    @Override
    public List<Unit> selectSecondaryTargets(Unit main, List<Unit> optionalTargets) {
        return skillParam.selectSecondaryTargets(main, optionalTargets);
    }

    @Override
    public List<Affect> processMainTarget(Unit source, Unit target, int processCount, int targetCount, DamageValue cost) {
        return skillPerformer.processMainTarget(source, target, processCount, skillParameterTable, level, targetCount, cost);
    }

    @Override
    public List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, int targetCount, DamageValue cost) {
        return skillPerformer.processSecondaryTarget(source, target, processCount, skillParameterTable, level, targetCount, cost);
    }

    @Override
    public boolean canManualUse() {
        return true;
//        return getType().equals(SkillType.FURY);
    }

    @Override
    public SkillParameterTable getSkillParameterTable() {
        return skillParameterTable;
    }

}
