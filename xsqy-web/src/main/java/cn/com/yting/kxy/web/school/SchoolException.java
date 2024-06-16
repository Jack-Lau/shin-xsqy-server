/*
 * Created 2018-9-14 15:44:35
 */
package cn.com.yting.kxy.web.school;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class SchoolException extends KxyWebException {

    public static final int EC_LEVEL_REACH_LIMIT = 1000;
    public static final int EC_INSUFFICIENT_CURRENCY = 1001;

    public SchoolException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static SchoolException levelReachLimit() {
        return new SchoolException(EC_LEVEL_REACH_LIMIT, "门派技能已达上限等级");
    }

    public static SchoolException insufficientCurrency() {
        return new SchoolException(EC_INSUFFICIENT_CURRENCY, "货币不足");
    }
}
