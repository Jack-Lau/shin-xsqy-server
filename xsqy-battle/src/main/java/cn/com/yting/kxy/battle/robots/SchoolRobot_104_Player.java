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
 * 五庄观的玩家Ai
 */
public class SchoolRobot_104_Player implements Robot {

    private final long id = 109401;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_104_Player() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        //
        int aliveEnemyCount = 0;
        aliveEnemyCount = allUnits.stream().filter((u) -> (!source.isAlly(u) && !u.isHpZero())).map((_item) -> 1).reduce(aliveEnemyCount, Integer::sum);
        if (RandomProvider.getRandom().nextDouble() < (0.7 - aliveEnemyCount * 0.1)) {
            int lowHpEnemyCount = 0;
            for (Unit u : allUnits) {
                if (!source.isAlly(u) && !u.isHpZero() && u.getHp().getRate() >= 0.15 && u.getHp().getRate() <= 0.25) {
                    lowHpEnemyCount++;
                }
            }
            if (lowHpEnemyCount == 1) {
                Skill yujianfumo = source.getSkill(104601);
                if (yujianfumo != null) {
                    return new UseSkillAction(yujianfumo, targetSelector.select(source, allUnits));
                }
            }
            if (lowHpEnemyCount > 1) {
                Skill shuangtianjianwu = source.getSkill(104501);
                if (shuangtianjianwu != null) {
                    return new UseSkillAction(shuangtianjianwu, targetSelector.select(source, allUnits));
                }
            }
        }
        if (aliveEnemyCount >= 3) {
            if (aliveEnemyCount == 3 && source.getParameter(ParameterNameConstants.五庄奇穴4_甲).getValue() > 0) {
                Skill shuangtianjianwu = source.getSkill(104501);
                if (shuangtianjianwu != null) {
                    return new UseSkillAction(shuangtianjianwu, targetSelector.select(source, allUnits));
                }
            }
            Skill jianyucangfeng = source.getSkill(104201);
            if (jianyucangfeng != null) {
                return new UseSkillAction(jianyucangfeng, targetSelector.select(source, allUnits));
            }
        }
        if (aliveEnemyCount >= 2) {
            Skill shuangtianjianwu = source.getSkill(104501);
            if (shuangtianjianwu != null) {
                return new UseSkillAction(shuangtianjianwu, targetSelector.select(source, allUnits));
            }
        }
        Skill yujianfumo = source.getSkill(104601);
        return new UseSkillAction(yujianfumo, targetSelector.select(source, allUnits));
    }

    @Override
    public long getId() {
        return this.id;
    }

}
