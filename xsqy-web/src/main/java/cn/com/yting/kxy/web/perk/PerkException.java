/*
 * Created 2019-1-17 11:28:07
 */
package cn.com.yting.kxy.web.perk;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class PerkException extends KxyWebException {

    public static final int EC_天赋记录已存在 = 3100;
    public static final int EC_等级不足 = 3101;
    public static final int EC_天赋培养已达上限 = 3102;
    public static final int EC_消耗量太低 = 3103;
    public static final int EC_位置未达到等级要求 = 3104;
    public static final int EC_位置已激活 = 3105;

    public PerkException(int errorCode, String message) {
        super(errorCode, message);
    }
}
