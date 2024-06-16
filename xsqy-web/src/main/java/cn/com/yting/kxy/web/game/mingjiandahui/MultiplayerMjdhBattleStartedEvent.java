/*
 * Created 2018-12-18 17:57:04
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.battle.multiplayer.MultiplayerBattleSession;

/**
 *
 * @author Azige
 */
public class MultiplayerMjdhBattleStartedEvent extends KxyWebEvent {

    private final MultiplayerBattleSession multiplayerBattleSession;

    public MultiplayerMjdhBattleStartedEvent(Object source, MultiplayerBattleSession multiplayerBattleSession) {
        super(source);
        this.multiplayerBattleSession = multiplayerBattleSession;
    }

    public MultiplayerBattleSession getMultiplayerBattleSession() {
        return multiplayerBattleSession;
    }
}
