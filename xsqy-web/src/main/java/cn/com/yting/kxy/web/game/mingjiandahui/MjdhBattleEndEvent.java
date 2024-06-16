/*
 * Created 2018-12-21 11:41:42
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.util.List;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class MjdhBattleEndEvent extends KxyWebEvent {

    private final List<Long> accountIds;
    private final MjdhBattleLog battleLog;

    public MjdhBattleEndEvent(Object source, List<Long> accountIds, MjdhBattleLog battleLog) {
        super(source);
        this.accountIds = accountIds;
        this.battleLog = battleLog;
    }

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public MjdhBattleLog getBattleLog() {
        return battleLog;
    }
}
