/*
 * Created 2016-5-16 17:23:33
 */
package cn.com.yting.kxy.battle.event;

import java.util.Queue;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.action.ActionChance;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class BeforeActionChanceEvent extends BattleEvent{

    public enum BeforeActionChanceEventType implements EventType<BeforeActionChanceEvent>{

        ACTION_BEFORE
    }

    private final Queue<ActionChance> actionChanceQueue;

    public BeforeActionChanceEvent(BattleDirector source, Queue<ActionChance> actionChanceQueue){
        this(BeforeActionChanceEventType.ACTION_BEFORE, source, actionChanceQueue);
    }

    public BeforeActionChanceEvent(EventType<? extends BeforeActionChanceEvent> type, BattleDirector source, Queue<ActionChance> actionChanceQueue){
        super(type, source);
        this.actionChanceQueue = actionChanceQueue;
    }

    public Queue<ActionChance> getActionChanceQueue(){
        return actionChanceQueue;
    }
}
