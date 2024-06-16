/*
 * Created 2015-10-9 13:33:33
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent.CheckBattleEndEventType;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

/**
 * 一方全灭的战斗结束条件
 *
 * @author Azige
 */
public class AllDeadBattleEndHandler implements EventHandler<CheckBattleEndEvent> {

    @Override
    public void handle(CheckBattleEndEvent event) {
        BattleDirector bd = event.getBattleDirector();
        if (!bd.getBlueParty().isAnyoneAlive() || !bd.getRedParty().isAnyoneAlive()) {
            boolean redWin = bd.getRedParty().isAnyoneAlive();
            event.setBattleEnd(true);
            if (redWin) {
                event.setWinStance(Unit.Stance.STANCE_RED);
            } else {
                event.setWinStance(Unit.Stance.STANCE_BLUE);
            }
        }
    }

    @Override
    public EventType<CheckBattleEndEvent> getHandleEventType() {
        return CheckBattleEndEventType.CHECK_BATTLE_END_AT_ACTION_END;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
