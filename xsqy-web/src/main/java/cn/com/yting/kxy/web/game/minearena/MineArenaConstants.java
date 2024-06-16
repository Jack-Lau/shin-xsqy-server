/*
 * Created 2018-10-17 17:06:21
 */
package cn.com.yting.kxy.web.game.minearena;

import java.time.Duration;

/**
 *
 * @author Azige
 */
public final class MineArenaConstants {

    /**
     * 位置变化记录中用于表示初始位置的值
     *
     * @see PitPositionChangeLog#getBeforePosition()
     */
    public static final long POSITION_INIT = -1;

    public static final int CHALLENGE_POINT_INIT = 1000;

    public static final long OUTER_RANKING_POSITION = 1001;
    public static final long OUTER_RANKING_CURRENCY_ID = 150;
    public static final double OUTER_RANKING_EFFICIENCY = 1.0;
    public static final int PLAYER_LEVEL_REQUIREMENT = 45;
    public static final Duration DURATION_MAX_CHALLENGE_TIME = Duration.ofMinutes(35);

    public static final long AWARD_ID_SUCCESS = 3071;
    public static final long AWARD_ID_FAILURE = 3072;
    
    public static final long PRICE_OBTAIN = 5_000;

    public static final long RATIO_YB_TO_CP = 1;

    private MineArenaConstants() {
    }

}
