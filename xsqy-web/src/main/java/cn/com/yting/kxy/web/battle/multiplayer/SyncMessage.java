/*
 * Created 2018-12-7 12:21:27
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@WebMessageType
public class SyncMessage implements Syncable {

    private MultiplayerBattleStatus syncStatus;
    private int syncNumber;
    private Object extra;
}
