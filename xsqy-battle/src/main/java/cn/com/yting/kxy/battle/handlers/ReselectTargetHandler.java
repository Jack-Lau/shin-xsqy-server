/*
 * Created 2016-4-14 17:07:47
 */
package cn.com.yting.kxy.battle.handlers;

import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.action.ActionChance;
import cn.com.yting.kxy.battle.action.ActionType;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.event.ActionChanceEvent;
import cn.com.yting.kxy.battle.event.ActionChanceEvent.ActionChanceEventType;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.random.RandomProvider;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class ReselectTargetHandler implements EventHandler<ActionChanceEvent>{

    @Override
    public void handle(ActionChanceEvent event){
        ActionChance actionChance = event.getActionChance();
        if (actionChance.getAction() != null && actionChance.getAction().getType().equals(ActionType.USE_SKILL)){
            UseSkillAction action = (UseSkillAction)actionChance.getAction();
            Unit actor = actionChance.getActor();
            Skill skill = action.getSkill();
            if (!checkTargetLegality(actor, skill, action.getTarget())){
                List<Unit> optionUnits = event.getBattleDirector().getAllUnits()
                    .filter(unit -> checkTargetLegality(actor, skill, unit))
                    .collect(Collectors.toList());
                if (optionUnits.isEmpty()){
                    action.setTarget(null);
                }else{
                    action.setTarget(optionUnits.get(RandomProvider.getRandom().nextInt(optionUnits.size())));
                }
            }
        }
    }

    private boolean checkTargetLegality(Unit actor, Skill skill, Unit target){
        // TODO: 技能检查目标有效性
        return target != null && target.isTargetable(actor);
    }

    @Override
    public EventType<ActionChanceEvent> getHandleEventType(){
        return ActionChanceEventType.ACTION_MODIFY;
    }

    @Override
    public int getPriority(){
        return 0;
    }
}
