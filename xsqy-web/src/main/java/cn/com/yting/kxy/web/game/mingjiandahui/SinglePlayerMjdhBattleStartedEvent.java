/*
 * Created 2018-12-18 17:55:22
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.battle.BattleSession;

/**
 *
 * @author Azige
 */
public class SinglePlayerMjdhBattleStartedEvent extends KxyWebEvent {

    private final long accountId;
    private final BattleSession battleSession;

    public SinglePlayerMjdhBattleStartedEvent(Object source, long accountId, BattleSession battleSession) {
        super(source);
        this.accountId = accountId;
        this.battleSession = battleSession;
    }

    public long getAccountId() {
        return accountId;
    }

    public BattleSession getBattleSession() {
        return battleSession;
    }
}
