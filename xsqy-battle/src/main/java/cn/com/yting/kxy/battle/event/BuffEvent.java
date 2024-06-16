/*
 * Created 2016-4-11 16:49:19
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.buff.Buff;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class BuffEvent extends BattleEvent{

    public enum BuffEventType implements EventType<BuffEvent>{

        BUFF_ATTACH,
        BUFF_DETACH,
        BUFF_DECAY
    }

    private final Buff buff;

    public BuffEvent(EventType<? extends BuffEvent> type, BattleDirector source, Buff buff){
        super(type, source);
        this.buff = buff;
    }

    public Buff getBuff(){
        return buff;
    }
}
