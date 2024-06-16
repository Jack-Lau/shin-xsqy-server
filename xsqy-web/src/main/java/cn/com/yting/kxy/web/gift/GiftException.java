/*
 * Created 2017-6-28 11:16:17
 */
package cn.com.yting.kxy.web.gift;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class GiftException extends KxyWebException {

    public static final int EC_GIFT_NOT_FOUND = 600;
    public static final int EC_GIFT_REDEEMED = 601;
    public static final int EC_GIFT_NOT_AVAILABLE = 602;
    public static final int EC_OVER_LIMITATION = 605;

    public GiftException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static GiftException giftNotFound() {
        return new GiftException(EC_GIFT_NOT_FOUND, "兑换码对应的礼包不存在");
    }

    public static GiftException giftRedeemed() {
        return new GiftException(EC_GIFT_REDEEMED, "礼包已被兑换");
    }

    public static GiftException giftNotAvailable() {
        return new GiftException(EC_GIFT_NOT_AVAILABLE, "礼包尚不可用");
    }

    public static GiftException overLimitation() {
        return new GiftException(EC_OVER_LIMITATION, "礼包兑换超过次数限制");
    }
}
