/*
 * Created 2018-9-26 16:18:58
 */
package cn.com.yting.kxy.web.party;

import java.time.Duration;

/**
 *
 * @author Azige
 */
public final class PartyConstants {

    public static final int MAX_SUPPORT_COUNT = 10;
    public static final int MAX_PARTY_MEMBER = 2;

    public static final int CANDIDATE_LIMIT = 50;

    public static final int REQUIREMENT_PLAYER_LEVEL = 35;

    public static final long INVITATION_FEE_BASE = 0;
    public static final double INVITATION_FEE_FC_RATE = 0.001;
    public static final double INVITATION_FEE_HIGH_LEVEL_RATE = 10;

    public static final Duration DURATION_SUPPORT = Duration.ofHours(3);
    public static final Duration DURATION_RELEASED_COOLDOWN = Duration.ofHours(1);

    public static final double REWARD_RATE = 0.9;
    public static final long DAILY_REWARD_GOLD_MAX = 10000;

    private PartyConstants() {
    }

}
