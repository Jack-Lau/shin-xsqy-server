/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleConstant;
import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.action.ActionChance;
import cn.com.yting.kxy.battle.action.PlayerSummonAction;
import cn.com.yting.kxy.battle.event.BeforeActionChanceEvent;
import cn.com.yting.kxy.battle.event.BeforeActionChanceEvent.BeforeActionChanceEventType;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Darkholme
 */
public class ActionBeforeHandler implements EventHandler<BeforeActionChanceEvent> {

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void handle(BeforeActionChanceEvent event) {
        BattleDirector battleDirector = event.getBattleDirector();
        ActionChance actionChance = event.getActionChanceQueue().peek();
        if (actionChance != null) {
            Unit actor = actionChance.getActor();
            if (!actor.isHpZero()
                    && !actor.isSummoned()
                    && actor.getBattlePetUnitQueue().size() > 0) {
                boolean petFlyOut = true;
                for (Unit u : battleDirector.getAllies(actor)) {
                    if (u.getPosition() == actor.getPosition() + BattleConstant.PET_POSITION_OFFSET
                            && !u.isFlyOut()) {
                        petFlyOut = false;
                        break;
                    }
                }
                if (petFlyOut) {
                    PlayerSummonAction playerSummonAction = new PlayerSummonAction();
                    playerSummonAction.perform(
                            actor,
                            battleDirector,
                            actor.getStance() == Unit.Stance.STANCE_RED ? battleDirector.getRedParty() : battleDirector.getBlueParty());
                }
            }
        }
    }

    @Override
    public EventType<BeforeActionChanceEvent> getHandleEventType() {
        return BeforeActionChanceEventType.ACTION_BEFORE;
    }

}
