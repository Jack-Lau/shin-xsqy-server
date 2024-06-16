/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.DefaultTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 * 盘丝洞的非玩家Ai
 */
public class SchoolRobot_103_NonPlayer implements Robot {

    private final long id = 109302;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_103_NonPlayer() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        if (allowUseFurySkill(source)) {
            long skillAId = 103501, skillBId = 103101;
            int currSp = (int) source.getSp().getValue();
            Skill skillA = source.getSkill(skillAId);
            Skill skillB = source.getSkill(skillBId);
            if (skillB != null) {
                if (currSp >= source.getSpCost(skillB.getCost().getSp())) {
                    int canAttachEnemyCount = 0;
                    Unit target = null;
                    for (Unit u : allUnits) {
                        if (!source.isAlly(u) && !u.isHpZero() && !u.hasBuff("六脉血逆") && !u.hasBuff("万蛊噬心") && !u.hasBuff("封魂咒") && u.getParameter(ParameterNameConstants.免疫封印).getValue() <= 0) {
                            canAttachEnemyCount++;
                            target = u;
                        }
                    }
                    if (canAttachEnemyCount >= 2
                            && RandomProvider.getRandom().nextDouble() < canAttachEnemyCount * 0.25) {
                        return new UseSkillAction(skillB, target);
                    }
                }
            }
            if (skillA != null) {
                if (currSp >= source.getSpCost(skillA.getCost().getSp())) {
                    int notHaveBuffAllyCount = 0;
                    for (Unit u : allUnits) {
                        if (source.isAlly(u) && !u.isHpZero() && !u.hasBuff("七煞诀")) {
                            notHaveBuffAllyCount++;
                        }
                        if (notHaveBuffAllyCount >= 3) {
                            return new UseSkillAction(skillA, u);
                        }
                    }
                }
            }
        }
        //
        int lowHpEnemyCount = 0;
        lowHpEnemyCount = allUnits.stream().filter((u) -> (!source.isAlly(u) && !u.isHpZero() && u.getHp().getRate() <= 0.1)).map((_item) -> 1).reduce(lowHpEnemyCount, Integer::sum);
        if (lowHpEnemyCount != 0) {
            Skill chuanhuajian = source.getSkill(103701);
            Unit target = targetSelector.select(source, allUnits);
            return new UseSkillAction(chuanhuajian, target);
        }
        if (RandomProvider.getRandom().nextDouble() < 0.2) {
            int aliveAllyCount = 0;
            aliveAllyCount = allUnits.stream().filter((u) -> (source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveAllyCount, Integer::sum);
            if (aliveAllyCount < 3) {
                Skill chuanhuajian = source.getSkill(103701);
                if (chuanhuajian != null) {
                    Unit target = targetSelector.select(source, allUnits);
                    if (target != null) {
                        return new UseSkillAction(chuanhuajian, target);
                    }
                }
            }
        }
        Unit unsealAlly = null;
        for (Unit u : allUnits) {
            if (source.isAlly(u) && !u.isHpZero() && (u.hasBuff("万蛊噬心") || u.hasBuff("封魂咒"))) {
                if (unsealAlly == null || u.getHp().getValue() > unsealAlly.getHp().getValue()) {
                    unsealAlly = u;
                }
            }
        }
        if (unsealAlly != null) {
            Skill liumaixueni = source.getSkill(103201);
            if (liumaixueni != null) {
                return new UseSkillAction(liumaixueni, unsealAlly);
            }
        }
        int sealedAliveEnemyCount = 0;
        sealedAliveEnemyCount = allUnits.stream().filter((u) -> (!source.isAlly(u) && !u.isHpZero() && (u.hasBuff("万蛊噬心") || u.hasBuff("封魂咒")))).map((_item) -> 1).reduce(sealedAliveEnemyCount, Integer::sum);
        if (sealedAliveEnemyCount > 2) {
            Skill chuanhuajian = source.getSkill(103701);
            if (chuanhuajian != null) {
                Unit target = targetSelector.select(source, allUnits);
                if (target != null) {
                    return new UseSkillAction(chuanhuajian, target);
                }
            }
        }
        Unit unsealedAliveEnemy = null;
        for (Unit u : allUnits) {
            if (!source.isAlly(u) && !u.isHpZero() && !(u.hasBuff("万蛊噬心") || u.hasBuff("封魂咒") || u.hasBuff("六脉血逆") || u.getParameter(ParameterNameConstants.免疫封印).getValue() > 0)) {
                if (unsealedAliveEnemy == null || u.getHp().getValue() > unsealedAliveEnemy.getHp().getValue()) {
                    unsealedAliveEnemy = u;
                }
            }
        }
        if (unsealedAliveEnemy != null) {
            Skill fenghunzhou = source.getSkill(103301);
            if (fenghunzhou != null) {
                return new UseSkillAction(fenghunzhou, unsealedAliveEnemy);
            }
        }
        Skill chuanhuajian = source.getSkill(103701);
        Unit target = targetSelector.select(source, allUnits);
        return new UseSkillAction(chuanhuajian, target);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public boolean allowUseFurySkill(Unit source) {
        if (source.getHp().getRate() < 0.3) {
            return true;
        }
        return RandomProvider.getRandom().nextDouble() <= 0.01 * source.getSp().getValue();
    }

}
