/*
 * Created 2019-2-21 12:56:10
 */
package cn.com.yting.kxy.web.topone;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class OrderNotificationEvent extends KxyWebEvent {

    private final OrderNotifyRequest request;

    public OrderNotificationEvent(Object source, OrderNotifyRequest request) {
        super(source);
        this.request = request;
    }

    public OrderNotifyRequest getRequest() {
        return request;
    }
}
