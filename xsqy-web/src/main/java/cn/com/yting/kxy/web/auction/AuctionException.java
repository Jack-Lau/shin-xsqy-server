/*
 * Created 2018-11-16 18:22:51
 */
package cn.com.yting.kxy.web.auction;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class AuctionException extends KxyWebException {

    public static final int EC_COMMODITY_NOT_ON_SALE = 1900;
    public static final int EC_SALE_TIME_UP = 1901;
    public static final int EC_INSUFFICIENT_CURRENCY = 1902;
    public static final int EC_NO_REPEAT_BID = 1903;
    public static final int EC_TOO_LOW_BID = 1904;
    public static final int EC_DAILY_LIKE_REACH_LIMIT = 1905;
    public static final int EC_LEVEL_NOT_MEET_REQUIREMENT = 1906;

    public AuctionException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static AuctionException commodityNotOnSale() {
        return new AuctionException(EC_COMMODITY_NOT_ON_SALE, "商品当前不在出售状态");
    }

    public static AuctionException saleTimeUp() {
        return new AuctionException(EC_SALE_TIME_UP, "商品当前已超过出售期限");
    }

    public static AuctionException insufficientCurrency() {
        return new AuctionException(EC_INSUFFICIENT_CURRENCY, "货币不足");
    }

    public static AuctionException noRepeatBid() {
        return new AuctionException(EC_NO_REPEAT_BID, "不能对已出价的商品再出价");
    }

    public static AuctionException tooLowBid() {
        return new AuctionException(EC_TOO_LOW_BID, "出价过低");
    }

    public static AuctionException dailyLikeReachLimit() {
        return new AuctionException(EC_DAILY_LIKE_REACH_LIMIT, "当日点赞已达到上限");
    }

    public static AuctionException levelNotMeetRequirement() {
        return new AuctionException(EC_LEVEL_NOT_MEET_REQUIREMENT, "等级未达到要求");
    }
}
