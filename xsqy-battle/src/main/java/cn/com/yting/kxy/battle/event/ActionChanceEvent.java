/*
 * Created 2016-4-11 16:47:02
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.action.ActionChance;
import cn.com.yting.kxy.battle.BattleDirector;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class ActionChanceEvent extends BattleEvent{

    public enum ActionChanceEventType implements EventType<ActionChanceEvent>{

        ACTION_START,
        ACTION_MODIFY,
        ACTION_IN,
        ACTION_END
    }

    private final ActionChance actionChance;

    public ActionChanceEvent(EventType<? extends ActionChanceEvent> type, BattleDirector source, ActionChance actionChance){
        super(type, source);
        this.actionChance = actionChance;
    }

    public ActionChance getActionChance(){
        return actionChance;
    }
}
