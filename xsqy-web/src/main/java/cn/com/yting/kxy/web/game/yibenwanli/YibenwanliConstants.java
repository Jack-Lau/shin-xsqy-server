/*
 * Created 2018-9-1 17:13:16
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.time.Duration;

/**
 *
 * @author Azige
 */
public final class YibenwanliConstants {

    public static final long POOL_MIN_VALUE = 100000;
    /**
     * 玩家购买本票的花费转换到奖池中的比例
     */
    public static final double RATE_POOL_CONVERSION = 0.95;
    /**
     * 最后一个购买的人分的奖金的比例
     */
    public static final double RATE_AWARD_FOR_LAST_ONE = 0.6;
    /**
     * 用于平分的奖金的比例
     */
    public static final double RATE_AWARD_FOR_PUBLIC = 0.34;
    /**
     * 幸运奖的比例
     */
    public static final double RATE_AWARD_FOR_LUCK = 0.06;
    /**
     * 开奖期限的最大值
     */
    public static final Duration DURATION_MAX_TO_DEADLINE = Duration.ofHours(24);
    /**
     * 每次购买将开奖期限向后推迟的时间
     */
    public static final Duration DURATION_PUSH_BACK_PER_PURCHASE = Duration.ofMinutes(10);
    /**
     * 每次购买的时间间隔
     */
    public static final Duration DURATION_PURCHASE_INTERVAL = Duration.ofSeconds(30);
    /**
     * 每轮结束后到下一轮的时间
     */
    public static final Duration DURATION_TO_NEXT_SEASON = Duration.ofMinutes(60);
    /**
     * 需要发广播通知活动即将结束的剩余时间限制
     */
    public static final Duration DURATION_LAST_CHANCE = Duration.ofHours(6);
    /**
     * 计算每票奖金时最小的除数
     */
    public static final int DIVISOR_MIN = 1;
    public static final long BROADCAST_ID_CONCLUSION = 3200003;
    public static final long BROADCAST_ID_START = 3200004;
    public static final long BROADCAST_ID_REACH_GOAL = 3200005;
    public static final long BROADCAST_ID_LAST_CHANCE = 3200006;
    public static final long BROADCAST_ID_LUCKY_ONE = 3200007;
    public static final long MAIL_ID_PUBLIC_AWARD = 16;
    public static final long MAIL_ID_LAST_ONE_AWARD = 17;
    public static final long MAIL_ID_LUCKY_ONE_AWARD = 18;
    public static final long[] POOL_GOALS = {200000, 300000, 500000, 800000, 1000000, 1500000, 2000000};

    public static final Duration DURATION_TO_DEADLINE_LIMIT_PAUSE_COMPENSATION = Duration.ofMinutes(15);
    public static final Duration DURATION_PAUSE_COMPENSATION = Duration.ofMinutes(15);

    public static final int PLAYER_LEVEL_REQUIRE = 50;

    private YibenwanliConstants() {
    }

}
