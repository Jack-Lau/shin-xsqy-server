/*
 * Created 2018-7-9 16:18:04
 */
package cn.com.yting.kxy.web.invitation;

/**
 *
 * @author Azige
 */
public final class InvitationConstants {

    /**
     * 直接邀请者的深度
     */
    public static final int DIRECT_INVITATION_DEPTH = 1;

    /**
     * 最大的邀请者的传递深度
     */
    public static final int MAX_INVITATION_DEPTH = 2;

    /**
     * 初始的邀请数量限制
     */
    public static final int DEFAULT_INVITATION_LIMIT = 10;

    /**
     * “创世居民”的初始邀请数量限制
     */
    public static final int DEFAULT_INVITATION_LIMIT_FOR_GENESIS = 30;

    /**
     * 邀请数量的最高上限
     */
    public static final int MAX_INVITATION_LIMIT = 100;

    /**
     * 结算邀请回报时获得的总能量上限
     */
    public static final int REWARD_UPPER_LIMIT_TOTAL = 1500;

    /**
     * 结算邀请回报时获得的总仙石上限
     */
    public static final int REWARD_XS_UPPER_LIMIT_TOTAL = 3000_000;

    /**
     * 结算邀请回报时从每个被邀请者获得的能量下限
     */
    public static final int REWARD_LOWER_LIMIT_EACH = 10;

    public static final double KUAIBI_INVITATION_REWARD_RATE = 0.1;
    public static final double KUAIBI_INVITATION_REWARD_RATE_FROM_PLAYER_INTERACTIVE = 0.03;

    /**
     * 每扩充一次邀请上限的消耗
     */
    public static final long COST_EXTEND_INVITATION_LIMIT = 10_000;

    public static final int GET_TITLE_INVITATION_COUNT = 30;

    public static final long INVITATION_TITLE_CURRENCY_ID = 20005;

    public static final long INVITATION_TITLE_MAIL_ID = 40;

    private InvitationConstants() {
    }
}
