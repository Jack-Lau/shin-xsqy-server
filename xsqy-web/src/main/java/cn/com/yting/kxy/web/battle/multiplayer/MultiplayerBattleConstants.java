/*
 * Created 2018-12-8 11:01:49
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import java.time.Duration;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Azige
 */
public final class MultiplayerBattleConstants {

    public static final Map<MultiplayerBattleStatus, Duration> STATUS_TO_TIMEOUT_MAP = ImmutableMap.<MultiplayerBattleStatus, Duration>builder()
        .put(MultiplayerBattleStatus.BEFORE_BATTLE, Duration.ofSeconds(5))
        .put(MultiplayerBattleStatus.PREPARED, Duration.ofSeconds(60))
        .put(MultiplayerBattleStatus.BEFORE_TURN, Duration.ofSeconds(15))
        .put(MultiplayerBattleStatus.AFTER_TURN, Duration.ofSeconds(60))
        .put(MultiplayerBattleStatus.AFTER_BATTLE, Duration.ofSeconds(60))
        .build();

    private MultiplayerBattleConstants() {
    }
}
