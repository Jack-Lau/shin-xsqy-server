/*
 * Created 2019-1-5 15:28:30
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class MultiplayerBattleException extends KxyWebException {

    public static final int EC_失去同步 = 2900;

    public MultiplayerBattleException(int errorCode, String message) {
        super(errorCode, message);
    }
}
