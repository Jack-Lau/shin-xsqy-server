/*
 * Created 2015-10-9 13:36:27
 */
package cn.com.yting.kxy.battle.handlers;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent.CheckBattleEndEventType;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

/**
 * 到达限制回合数的战斗结束条件
 *
 * @author Azige
 */
public class TurnLimitedBattleEndHandler implements EventHandler<CheckBattleEndEvent> {

    private final int limitTurnCount;

    public TurnLimitedBattleEndHandler(int limitTurnCount) {
        this.limitTurnCount = limitTurnCount;
    }

    @Override
    public void handle(CheckBattleEndEvent event) {
        BattleDirector bd = event.getBattleDirector();
        if (bd.getTurnCount() >= limitTurnCount) {
            event.setBattleEnd(true);
            int redAliveCount = 0, blueAliveCount = 0;
            redAliveCount = bd.getRedParty().getUnitMap().values().stream().filter((unit) -> (!unit.isHpZero())).map((_item) -> 1).reduce(redAliveCount, Integer::sum);
            blueAliveCount = bd.getBlueParty().getUnitMap().values().stream().filter((unit) -> (!unit.isHpZero())).map((_item) -> 1).reduce(blueAliveCount, Integer::sum);
            if (redAliveCount > blueAliveCount) {
                event.setWinStance(Unit.Stance.STANCE_RED);
            } else if (blueAliveCount > redAliveCount) {
                event.setWinStance(Unit.Stance.STANCE_BLUE);
            } else {

            }
        }
    }

    public int getLimitTurnCount() {
        return limitTurnCount;
    }

    @Override
    public EventType<CheckBattleEndEvent> getHandleEventType() {
        return CheckBattleEndEventType.CHECK_BATTLE_END_AT_TURN_END;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
