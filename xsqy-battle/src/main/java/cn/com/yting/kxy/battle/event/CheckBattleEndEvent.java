/*
 * Created 2016-4-11 16:50:28
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit.Stance;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class CheckBattleEndEvent extends BattleEvent {

    public enum CheckBattleEndEventType implements EventType<CheckBattleEndEvent> {

        CHECK_BATTLE_END_AT_ACTION_END,
        CHECK_BATTLE_END_AT_TURN_END

    }

    private boolean battleEnd = false;
    private Stance winStance = Stance.STANCE_RED;

    public CheckBattleEndEvent(EventType<? extends CheckBattleEndEvent> type, BattleDirector source) {
        super(type, source);
    }

    public boolean isBattleEnd() {
        return battleEnd;
    }

    public void setBattleEnd(boolean battleEnd) {
        this.battleEnd = battleEnd;
    }

    /**
     * @return the winStance
     */
    public Stance getWinStance() {
        return winStance;
    }

    /**
     * @param winStance the winStance to set
     */
    public void setWinStance(Stance winStance) {
        this.winStance = winStance;
    }

}
