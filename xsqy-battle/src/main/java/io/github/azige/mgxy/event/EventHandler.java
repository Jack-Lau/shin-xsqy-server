/*
 * Created 2015-10-16 15:32:01
 */
package io.github.azige.mgxy.event;

import java.util.EventListener;
import java.util.function.Consumer;

/**
 * 事件处理器，要求是 <i>无状态</i> 的
 *
 * @author Azige
 * @param <T>
 */
public interface EventHandler<T extends Event> extends EventListener, Comparable<EventHandler>{

    /**
     * 事件处理程序。
     *
     * @param event 事件
     */
    void handle(T event);

    /**
     * 获得此事件处理器所处理的事件类型。
     *
     * @return 事件类型
     */
    EventType<T> getHandleEventType();

    /**
     * 获得此事件处理器的优先级。
     *
     * @return 优先级
     */
    int getPriority();

    /**
     * 与另一个对象比较，以优先级进行。
     *
     * @param o 要比较的对象
     * @return 优先级的比较值
     */
    @Override
    default int compareTo(EventHandler o){
        return this.getPriority() - o.getPriority();
    }

    /**
     * 把一个 {@link Consumer} 对象直接包装成事件监听器。
     *
     * @param <S>       监听的事件对象类型
     * @param handler   事件处理程序
     * @param eventType 事件类型
     * @param priority  优先级
     * @return 包装的事件处理器
     */
    static <S extends Event> EventHandler<S> of(Consumer<S> handler, EventType<S> eventType, int priority){
        return new EventHandler<S>(){

            @Override
            public void handle(S event){
                handler.accept(event);
            }

            @Override
            public EventType getHandleEventType(){
                return eventType;
            }

            @Override
            public int getPriority(){
                return priority;
            }
        };
    }
}
