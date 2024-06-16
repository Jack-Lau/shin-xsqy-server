/*
 * Created 2018-11-23 17:02:43
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class MineArenaChallengeCompletedEvent extends KxyWebEvent {

    private final MineArenaChallengeLog mineArenaChallengeLog;

    public MineArenaChallengeCompletedEvent(Object source, MineArenaChallengeLog mineArenaChallengeLog) {
        super(source);
        this.mineArenaChallengeLog = mineArenaChallengeLog;
    }

    public MineArenaChallengeLog getMineArenaChallengeLog() {
        return mineArenaChallengeLog;
    }
}
