/*
 * Created 2016-4-11 16:26:59
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.BattleDirector;
import io.github.azige.mgxy.event.Event;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class BattleEvent extends Event{

    public enum BattleEventType implements EventType<BattleEvent>{

        BATTLE_START,
        TURN_START,
        TURN_END,
        BATTLE_END
    }

    public BattleEvent(EventType<? extends BattleEvent> type, BattleDirector source){
        super(type, source);
    }

    @Override
    public BattleDirector getSource(){
        return (BattleDirector)super.getSource();
    }

    public BattleDirector getBattleDirector(){
        return getSource();
    }
}
