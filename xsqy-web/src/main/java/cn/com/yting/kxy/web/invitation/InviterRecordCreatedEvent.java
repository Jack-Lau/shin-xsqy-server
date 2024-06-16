/*
 * Created 2018-7-10 18:50:22
 */
package cn.com.yting.kxy.web.invitation;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class InviterRecordCreatedEvent extends KxyWebEvent {

    private final long accountId;
    private final boolean invited;

    public InviterRecordCreatedEvent(Object source, long accountId, boolean invited) {
        super(source);
        this.accountId = accountId;
        this.invited = invited;
    }

    public long getAccountId() {
        return accountId;
    }

    public boolean isInvited() {
        return invited;
    }
}
