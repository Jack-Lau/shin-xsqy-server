/*
 * Created 2018-10-31 15:34:26
 */
package cn.com.yting.kxy.web.ranking;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Azige
 */
public final class RankingConstants {

    public static final long MAIL_ID_NO_AWARD = 38;

    public static final long RANKING_ID_名剑大会 = 4430009;

    public static final Set<Long> AUTO_RESOLVE_DISABLED_IDS = ImmutableSet.of(
        RANKING_ID_名剑大会
    );

    private RankingConstants() {
    }
}
