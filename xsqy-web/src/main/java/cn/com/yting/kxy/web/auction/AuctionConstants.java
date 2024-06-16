/*
 * Created 2018-11-12 18:51:59
 */
package cn.com.yting.kxy.web.auction;

import java.time.Duration;

import cn.com.yting.kxy.web.player.PlayerRepository;

/**
 *
 * @author Azige
 */
public final class AuctionConstants {

    public static final int PLAYER_LEVEL_LIMIT = 50;

    public static final Duration DURATION_RESUME_NEED_EXTRA_TIME = Duration.ofMinutes(15);
    public static final Duration DURATION_RESUME_EXTRA_TIME = Duration.ofMinutes(30);
    public static final Duration DURATION_BID_NEED_EXTRA_TIME = Duration.ofMinutes(10);
    public static final Duration DURATION_BID_EXTRA_TIME = Duration.ofMinutes(1);

    public static final int COUNT_MAX_ON_SALE = 6;

    public static final long BROADCAST_ID_TITLE = 3200023;
    public static final long BROADCAST_ID_EQUIPMENT = 3200024;
    public static final long BROADCAST_ID_PET = 3200025;
    public static final long BROADCAST_ID_CURRENCY = 3200026;

    public static final long MAIL_ID_CONCLUSION_BIDDER = 42;
    public static final long MAIL_ID_CONCLUSION_LIKE_AWARD = 43;
    public static final long MAIL_ID_CONCLUSION_NO_LIKE_AWARD = 44;

    public static int dailyLikeUpperLimit(PlayerRepository playerRepository, long accountId) {
        return (int) (playerRepository.findById(accountId).get().getFc() / 10000) + 1;
    }

    private AuctionConstants() {
    }
}
