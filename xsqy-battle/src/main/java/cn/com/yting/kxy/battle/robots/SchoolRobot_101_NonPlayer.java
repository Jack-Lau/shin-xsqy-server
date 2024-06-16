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
import cn.com.yting.kxy.core.random.RandomProvider;
import java.util.ArrayList;

/**
 * 凌霄殿的非玩家Ai
 */
public class SchoolRobot_101_NonPlayer implements Robot {

    private final long id = 109102;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_101_NonPlayer() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        if (allowUseFurySkill(source)) {
            long skillAId = 101101, skillBId = 101401;
            int currSp = (int) source.getSp().getValue();
            Skill skillA = source.getSkill(skillAId);
            Skill skillB = source.getSkill(skillBId);
            int aliveEnemyCount = 0;
            aliveEnemyCount = allUnits.stream().filter((u) -> (!source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveEnemyCount, Integer::sum);
            if (skillA != null) {
                if (currSp >= source.getSpCost(skillA.getCost().getSp())) {
                    if (aliveEnemyCount >= 3 && RandomProvider.getRandom().nextDouble() < 0.5 * (aliveEnemyCount - 2)) {
                        Unit target = targetSelector.select(source, allUnits);
                        if (target != null) {
                            return new UseSkillAction(skillA, target);
                        }
                    }
                }
            }
            if (skillB != null) {
                if (currSp >= source.getSpCost(skillB.getCost().getSp())) {
                    if (aliveEnemyCount == 1) {
                        Unit target = targetSelector.select(source, allUnits);
                        if (target != null) {
                            return new UseSkillAction(skillB, target);
                        }
                    }
                    List<Unit> targets = new ArrayList<>();
                    for (Unit u : allUnits) {
                        if (!source.isAlly(u) && !u.isHpZero()) {
                            if (u.getHp().getRate() >= 0.5) {
                                targets.add(u);
                            }
                        }
                    }
                    if (targets.size() > 0) {
                        return new UseSkillAction(skillB, targets.get(RandomProvider.getRandom().nextInt(targets.size())));
                    } else {
                        if (skillA != null) {
                            if (currSp >= source.getSpCost(skillA.getCost().getSp())) {
                                Unit target = targetSelector.select(source, allUnits);
                                if (target != null) {
                                    return new UseSkillAction(skillA, target);
                                }
                            }
                        }
                    }
                }
            }
        }
        //
        Unit target = targetSelector.select(source, allUnits);
        if (target != null) {
            Skill ningxueren = source.getSkill(101201);
            if (ningxueren != null) {
                if (target.getHp().getRate() >= 0.1 && target.getHp().getRate() <= 0.2) {
                    return new UseSkillAction(ningxueren, target);
                }
            }
            //
            Skill qianjunji = source.getSkill(101501);
            if (qianjunji != null) {
                if ((target.getHp().getRate() >= 0.2 && target.getHp().getRate() <= 0.3)
                        || RandomProvider.getRandom().nextDouble() < target.getHp().getRate()) {
                    return new UseSkillAction(qianjunji, target);
                }
            }
            //
            Skill lingxiaojian = source.getSkill(101301);
            if (lingxiaojian != null) {
                int aliveEnemyCount = 0;
                aliveEnemyCount = allUnits.stream().filter((u) -> (!u.getIsDead() && !source.isAlly(u))).map((_item) -> 1).reduce(aliveEnemyCount, Integer::sum);
                if (aliveEnemyCount > 1) {
                    return new UseSkillAction(lingxiaojian, target);
                }
            }
            //
            return new UseSkillAction(qianjunji, target);
        } else {
            return null;
        }
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
