/*
 * Created 2018-12-10 15:42:34
 */
package cn.com.yting.kxy.web.battle.multiplayer;

/**
 *
 * @author Azige
 */
public interface Syncable {

    MultiplayerBattleStatus getSyncStatus();

    int getSyncNumber();

    default boolean isSyncedWith(Syncable other) {
        return this.getSyncStatus().equals(other.getSyncStatus()) && this.getSyncNumber() == other.getSyncNumber();
    }

    static Syncable simple(MultiplayerBattleStatus syncStatus, int syncNumber) {
        return new Syncable() {
            @Override
            public MultiplayerBattleStatus getSyncStatus() {
                return syncStatus;
            }

            @Override
            public int getSyncNumber() {
                return syncNumber;
            }
        };
    }
}
