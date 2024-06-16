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

/**
 * 凌霄殿的玩家Ai
 */
public class SchoolRobot_101_Player implements Robot {

    private final long id = 109101;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_101_Player() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
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

}
