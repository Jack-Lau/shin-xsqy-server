/*
 * Created 2018-8-31 17:06:43
 */
package cn.com.yting.kxy.web.ethereum;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Azige
 */
public class DepositCompletedEvent extends ApplicationEvent {

    private final DepositRequest request;

    public DepositCompletedEvent(Object source, DepositRequest request) {
        super(source);
        this.request = request;
    }

    public DepositRequest getRequest() {
        return request;
    }
}
