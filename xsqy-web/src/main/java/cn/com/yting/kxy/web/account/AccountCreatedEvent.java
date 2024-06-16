/*
 * Created 2018-7-6 16:54:30
 */
package cn.com.yting.kxy.web.account;

import cn.com.yting.kxy.web.KxyWebEvent;
import lombok.Getter;

/**
 *
 * @author Azige
 */
public class AccountCreatedEvent extends KxyWebEvent {

    @Getter
    private final Account account;

    public AccountCreatedEvent(Object source, Account account) {
        super(source);
        this.account = account;
    }
}
