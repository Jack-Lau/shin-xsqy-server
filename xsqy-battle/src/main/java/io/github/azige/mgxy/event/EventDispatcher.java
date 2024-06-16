/*
 * Created 2015-10-16 15:07:46
 */
package io.github.azige.mgxy.event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 事件分发器，将事件处理器注册到其中，然后由其分发事件。
 * 事件处理器接收事件的顺序由事件处理器的{@link EventHandler#getPriority() 优先级}决定。
 *
 * @author Azige
 */
public class EventDispatcher {

    private final Map<EventType<?>, Collection<EventHandler<?>>> eventHandlerMap = new HashMap<>();
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    private final Queue<EventHandler> firingEventHandlerQueue = new PriorityQueue<>(Comparator.reverseOrder());
    private EventType<?> firingEventType = null;

    public EventDispatcher() {
    }

    /**
     * 添加一个事件处理器的监听。 在事件处理中仍然可以调用此方法，如果监听的是相同的事件，
     * 此事件处理器会收到这个事件的触发，并且排在事件处理队列的最后。
     *
     * @param handler 要注册的事件处理器
     */
    public void addHandler(EventHandler<?> handler) {
        EventType<?> key = handler.getHandleEventType();
        Collection<EventHandler<?>> handlers = eventHandlerMap.get(key);
        if (handlers == null) {
            handlers = new ArrayList<>();
            eventHandlerMap.put(key, handlers);
        }
        handlers.add(handler);

        if (key.equals(firingEventType)) {
            firingEventHandlerQueue.offer(handler);
        }
    }

    /**
     * 移除一个事件处理器的全部的监听。 如果当前正在事件触发中，且此处理器能接收此事件但还未接收到触发， 则其不会再收到触发。
     *
     * @param handler 要移除的事件处理器
     */
    public void removeHandler(EventHandler<?> handler) {
        EventType<?> key = handler.getHandleEventType();
        Collection<EventHandler<?>> handlers = eventHandlerMap.get(key);
        if (handlers != null) {
            handlers.remove(handler);
        }
        if (key.equals(firingEventType)) {
            firingEventHandlerQueue.remove(handler);
        }
    }

    /**
     * 向事件队列中排入一个事件，如果当前不在事件触发中，则会立即进行。
     *
     * @param event 要提交的事件
     */
    public void enqueueEvent(Event event) {
        eventQueue.offer(event);
        if (firingEventType == null) {
            fireEvent();
        }
    }

    /**
     * 立即触发事件队列中的所有事件
     */
    private void fireEvent() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            EventType<?> key = event.getType();
            Collection<EventHandler<?>> handlers = eventHandlerMap.get(key);
            if (handlers != null) {
                firingEventHandlerQueue.addAll(handlers);
            }
            firingEventType = key;

            while (!firingEventHandlerQueue.isEmpty()) {
                EventHandler handler = firingEventHandlerQueue.poll();
                if (handler != null) {
                    handler.handle(event);
                }
            }

            firingEventType = null;
        }
    }
}
