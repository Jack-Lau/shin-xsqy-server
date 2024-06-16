/*
 * Created 2018-11-2 18:47:15
 */
package cn.com.yting.kxy.web.game.treasure;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class TreasureObtainedEvent extends KxyWebEvent {

    private final long accountId;

    public TreasureObtainedEvent(Object source, long accountId) {
        super(source);
        this.accountId = accountId;
    }

    public long getAccountId() {
        return accountId;
    }
}
