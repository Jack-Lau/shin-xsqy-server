/*
 * Created 2018-8-11 11:31:24
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
@WebMessageType
public class PlayerLevelupEvent extends KxyWebEvent {

    private final long accountId;
    private final int beforeLevel;
    private final int afterLevel;

    public PlayerLevelupEvent(Object source, long accountId, int beforeLevel, int afterLevel) {
        super(source);
        this.accountId = accountId;
        this.beforeLevel = beforeLevel;
        this.afterLevel = afterLevel;
    }
}
