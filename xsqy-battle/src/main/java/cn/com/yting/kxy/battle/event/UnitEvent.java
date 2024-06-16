/*
 * Created 2016-4-11 16:44:59
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class UnitEvent extends BattleEvent{

    public enum UnitEventType implements EventType<UnitEvent>{

        UNIT_ATTENDING
    }

    private final Unit unit;

    public UnitEvent(EventType<? extends UnitEvent> type, BattleDirector source, Unit unit){
        super(type, source);
        this.unit = unit;
    }

    public Unit getUnit(){
        return unit;
    }
}
