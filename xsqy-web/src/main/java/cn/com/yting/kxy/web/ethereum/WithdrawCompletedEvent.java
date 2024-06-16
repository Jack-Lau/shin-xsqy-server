/*
 * Created 2018-8-28 19:15:11
 */
package cn.com.yting.kxy.web.ethereum;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Azige
 */
public class WithdrawCompletedEvent extends ApplicationEvent {

    private final WithdrawRequest request;

    public WithdrawCompletedEvent(Object source, WithdrawRequest request) {
        super(source);
        this.request = request;
    }

    public WithdrawRequest getRequest() {
        return request;
    }

}
