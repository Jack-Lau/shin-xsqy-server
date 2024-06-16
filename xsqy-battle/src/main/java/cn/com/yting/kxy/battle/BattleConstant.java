/*
 * Created 2016-5-17 12:44:07
 */
package cn.com.yting.kxy.battle;

/**
 *
 * @author Azige
 */
public final class BattleConstant {

    /**
     * 宠物与玩家的位置偏移，以位置来确定宠物的所属。
     */
    public static final int PET_POSITION_OFFSET = 3;

    public static enum FURY_MODEL {
        NONE,
        ASYNC_PVP_SINGLE,
        ASYNC_PVP_TEAM,
        SYNC_PVP_SINGLE,
        SYNC_PVP_TEAM
    }

    private BattleConstant() {

    }

}
