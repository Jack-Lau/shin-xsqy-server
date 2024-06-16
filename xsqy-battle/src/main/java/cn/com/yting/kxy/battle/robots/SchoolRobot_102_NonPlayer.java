/*
 * Created 2015-11-2 17:48:27
 */
package cn.com.yting.kxy.battle.robots;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.DefaultTargetSelector;
import cn.com.yting.kxy.battle.robot.RecoverTargetSelector;
import cn.com.yting.kxy.battle.robot.ReviveTargetSelector;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.TargetSelector;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 * 普陀山的非玩家Ai
 */
public class SchoolRobot_102_NonPlayer implements Robot {

    private final long id = 109202;
    private final TargetSelector targetSelector = new DefaultTargetSelector();
    private final TargetSelector recoverTargetSelector = new RecoverTargetSelector();
    private final TargetSelector reviveTargetSelector = new ReviveTargetSelector();

    public SchoolRobot_102_NonPlayer() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        if (allowUseFurySkill(source)) {
            long skillAId = 102701, skillBId = 102702, skillCId = 102201;
            int currSp = (int) source.getSp().getValue();
            Skill skillA = source.getSkill(skillAId);
            Skill skillB = source.getSkill(skillBId);
            Skill skillC = source.getSkill(skillCId);
            if (skillC != null) {
                if (currSp >= source.getSpCost(skillC.getCost().getSp())) {
                    int lowHpAllyCount = 0, lessHpAllyCount = 0, notFullHpAllyCount = 0;
                    for (Unit u : allUnits) {
                        if (source.isAlly(u) && !u.isHpZero()) {
                            if (u.getHp().getRate() <= 0.8) {
                                notFullHpAllyCount++;
                            }
                            if (u.getHp().getRate() <= 0.6) {
                                lessHpAllyCount++;
                            }
                            if (u.getHp().getRate() <= 0.4) {
                                lowHpAllyCount++;
                            }
                        }
                    }
                    if (notFullHpAllyCount >= 4 || lessHpAllyCount >= 3 || lowHpAllyCount >= 2) {
                        return new UseSkillAction(skillC, recoverTargetSelector.select(source, allUnits));
                    }
                }
            }
            if (skillA != null) {
                if (currSp >= source.getSpCost(skillA.getCost().getSp())) {
                    Unit target = reviveTargetSelector.select(source, allUnits);
                    if (target != null) {
                        return new UseSkillAction(skillA, target);
                    }
                    target = recoverTargetSelector.select(source, allUnits);
                    if (target != null) {
                        if (target.getHp().getRate() <= 0.5) {
                            return new UseSkillAction(skillA, target);
                        }
                    }
                }
            }
            if (skillB != null) {
                if (currSp >= source.getSpCost(skillB.getCost().getSp())) {
                    Unit target = null;
                    for (Unit u : allUnits) {
                        if (!source.isAlly(u) && !u.isHpZero()) {
                            if (u.getHp().getRate() >= 0.2 && u.getHp().getRate() <= 0.5) {
                                if (target == null
                                        || u.getHp().getRate() < target.getHp().getRate()) {
                                    target = u;
                                }
                            }
                        }
                    }
                    if (target != null) {
                        return new UseSkillAction(skillB, target);
                    }
                }
            }
        }
        //
        Skill yunhaichaosheng = source.getSkill(102401);
        if (yunhaichaosheng != null) {
            int aliveAllyCount = 0;
            aliveAllyCount = allUnits.stream().filter((u) -> (source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveAllyCount, Integer::sum);
            if (aliveAllyCount < 3) {
                if (RandomProvider.getRandom().nextDouble() < (1 - aliveAllyCount * 0.33)) {
                    return new UseSkillAction(yunhaichaosheng, targetSelector.select(source, allUnits));
                }
            }
        }
        Skill huifengyinlu = source.getSkill(102301);
        if (huifengyinlu != null) {
            int lowHpAllyCount = 0, lessHpAllyCount = 0;
            for (Unit u : allUnits) {
                if (source.isAlly(u) && !u.isHpZero()) {
                    if (u.getHp().getRate() <= 0.8) {
                        lessHpAllyCount++;
                    }
                    if (u.getHp().getRate() <= 0.6) {
                        lowHpAllyCount++;
                    }
                }
            }
            if (lessHpAllyCount >= 3 || lowHpAllyCount >= 2) {
                return new UseSkillAction(huifengyinlu, recoverTargetSelector.select(source, allUnits));
            }
        }
        Skill difanxianlu = source.getSkill(102601);
        if (difanxianlu != null) {
            Unit target = recoverTargetSelector.select(source, allUnits);
            if (target != null && target.getHp().getRate() <= 0.6) {
                return new UseSkillAction(difanxianlu, target);
            }
        }
        return new UseSkillAction(yunhaichaosheng, targetSelector.select(source, allUnits));
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
