/*
 * Created 2019-1-28 22:48:56
 */
package cn.com.yting.kxy.web.legendarypet;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class LegendaryPetException extends KxyWebException {

    public static final int EC_数量不足 = 3300;
    public static final int EC_已兑换的数量太多 = 3301;
    public static final int EC_货币不正确 = 3302;
    public static final int EC_兑换所需角色等级不足 = 3303;
    public static final int EC_进阶所需角色等级不足 = 3304;

    public LegendaryPetException(int errorCode, String message) {
        super(errorCode, message);
    }
}
