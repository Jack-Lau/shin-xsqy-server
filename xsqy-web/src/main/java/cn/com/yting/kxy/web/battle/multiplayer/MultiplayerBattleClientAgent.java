/*
 * Created 2018-12-7 11:25:36
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class MultiplayerBattleClientAgent implements Syncable {

    private final long accountId;

    private MultiplayerBattleStatus syncStatus = MultiplayerBattleStatus.INIT;
    private int syncNumber;
    private boolean lost;

    public void syncWith(MultiplayerBattleSession session) {
        this.syncStatus = session.getSyncStatus();
        this.syncNumber = session.getSyncNumber();
        this.lost = false;
    }
}
