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
 * 五庄观的非玩家Ai
 */
public class SchoolRobot_104_Test implements Robot {

    private final long id = 109403;
    private final TargetSelector targetSelector = new DefaultTargetSelector();

    public SchoolRobot_104_Test() {

    }

    @Override
    public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
        return null;
    }

    @Override
    public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
        if (allowUseFurySkill(source)) {
            long skillBId = 104101;
            int currSp = (int) source.getSp().getValue();
            Skill skillB = source.getSkill(skillBId);
            if (skillB != null) {
                if (currSp >= source.getSpCost(skillB.getCost().getSp())) {
                    Unit target = targetSelector.select(source, allUnits);
                    if (target != null) {
                        return new UseSkillAction(skillB, target);
                    }
                }
            }
        }
        //
        Skill yujianfumo = source.getSkill(104601);
        return new UseSkillAction(yujianfumo, targetSelector.select(source, allUnits));
    }

    @Override
    public long getId() {
        return this.id;
    }

    public boolean allowUseFurySkill(Unit source) {
        if (source.getHp().getRate() < 0.3) {
            return true;
        }
        if (source.getSp().getValue() >= 50) {
            if (RandomProvider.getRandom().nextDouble() <= 0.5) {
                return true;
            }
        }
        return false;
    }

}
