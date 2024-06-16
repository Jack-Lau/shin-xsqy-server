/*
 * Created 2015-10-16 15:23:50
 */
package io.github.azige.mgxy.event;

import java.util.EventObject;

/**
 * 事件基类，只是作为类型限定。
 *
 * @author Azige
 */
public class Event extends EventObject{

    private final EventType<?> type;

    public Event(EventType<?> type, Object source){
        super(source);
        this.type = type;
    }

    public EventType<?> getType(){
        return type;
    }
}
