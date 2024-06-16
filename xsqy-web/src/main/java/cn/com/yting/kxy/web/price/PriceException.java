/*
 * Created 2018-9-22 15:47:12
 */
package cn.com.yting.kxy.web.price;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class PriceException extends KxyWebException {

    public static final int EC_EXPECTED_PRICE_NOT_MATCH = 1100;
    public static final int EC_INSUFFICIENT_ACTIVE_POINT = 1101;

    public PriceException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static PriceException expectedPriceNotMatch() {
        throw new PriceException(EC_EXPECTED_PRICE_NOT_MATCH, "期望的价格与当前价格不同");
    }

    public static PriceException insufficientActivePoint() {
        throw new PriceException(EC_INSUFFICIENT_ACTIVE_POINT, "活跃点不足");
    }

}
