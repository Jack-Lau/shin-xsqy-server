/*
 * Created 2018-7-6 17:51:29
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

/**
 *
 * @author Azige
 */
public final class KbdzpConstants {

    public static long[] RECOVER_TIME_INTERVALS = {
        120 * 1000,
        90 * 1000,
        60 * 1000
    };
    public static final long MAX_TURN_COUNT_PER_DAY = 30;
    public static final int ENERGY_RECOVER_MAX_VALUE = 1000;
    public static final int ENERGY_COST_PER_TURN = 100;
    public static final double INVITATION_REWARD_RATE = 0.2;
    public static final int INVITEE_BONUS = 100;
    public static final int PUBLIC_POOL_RECOVER_PER_HOUR = 16600_000;
    public static final int PUBLIC_POOL_RECOVER_UPPER_LIMIT = 1000000_000;
    public static final int PERSONAL_POOL_RESET_VALUE = 20_000;
    public static final int NORMAL_KC_PROBABILITY_PLAYER_LEVEL = 60;
    public static final long NORMAL_KC_PROBABILITY_PLAYER_FC = 50000;
    public static final long GET_TITLE_TOTAL_GAIN_MILLI_KC = 888000;
    public static final long GET_TITLE_TOTAL_TURN_COUNT = 88;
    public static final long TITLE_CURRENCY_ID = 20006;
    public static final long TITLE_MAIL_ID = 41;

    private KbdzpConstants() {
    }
}
