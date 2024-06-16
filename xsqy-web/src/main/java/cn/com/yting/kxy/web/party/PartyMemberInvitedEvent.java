/*
 * Created 2018-11-23 18:11:07
 */
package cn.com.yting.kxy.web.party;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class PartyMemberInvitedEvent extends KxyWebEvent {

    private final long accountId;

    public PartyMemberInvitedEvent(Object source, long accountId) {
        super(source);
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}
