/*
 * Created 2018-9-3 18:54:00
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class YibenwanliException extends KxyWebException {

    public static final int EC_UNMATCHED_PRICE = 900;
    public static final int EC_INSUFFICIENT_CURRENCY = 901;
    public static final int EC_TIMEUP = 902;
    public static final int EC_PURCHASE_TOO_FAST = 903;
    public static final int EC_等级不足 = 904;

    public YibenwanliException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static YibenwanliException unmatchedPrice() {
        return new YibenwanliException(EC_UNMATCHED_PRICE, "价格不匹配");
    }

    public static YibenwanliException insufficientCurrency() {
        return new YibenwanliException(EC_INSUFFICIENT_CURRENCY, "货币不足");
    }

    public static YibenwanliException timeup() {
        return new YibenwanliException(EC_TIMEUP, "本期已结束");
    }

    public static YibenwanliException purchaseTooFast() {
        return new YibenwanliException(EC_PURCHASE_TOO_FAST, "购买时间间隔未到");
    }

    public static YibenwanliException 等级不足() {
        return new YibenwanliException(EC_等级不足, "等级不足");
    }

}
