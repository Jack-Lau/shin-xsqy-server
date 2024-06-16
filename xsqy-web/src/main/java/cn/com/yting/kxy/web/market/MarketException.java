/*
 * Created 2018-12-29 12:56:46
 */
package cn.com.yting.kxy.web.market;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class MarketException extends KxyWebException {

    public static final int EC_等级不足 = 2800;
    public static final int EC_战斗力不足 = 2801;
    public static final int EC_货品数量已达上限 = 2802;
    public static final int EC_装备正在装着中 = 2803;
    public static final int EC_装备交易冷却时间未到 = 2804;
    public static final int EC_装备的品质不对 = 2805;
    public static final int EC_宠物正在出战中 = 2806;
    public static final int EC_宠物交易冷却时间未到 = 2807;
    public static final int EC_宠物的品质不对 = 2808;
    public static final int EC_称号正在使用中 = 2809;
    public static final int EC_称号类型不对 = 2810;
    public static final int EC_称号的交易冷却时间未到 = 2811;
    public static final int EC_货品不是上架中状态 = 2812;
    public static final int EC_货品不是已下架状态 = 2813;
    public static final int EC_不能关注自己的货品 = 2814;
    public static final int EC_不能购买自己的货品 = 2815;
    public static final int EC_货品不是已售出状态 = 2816;
    public static final int EC_货款已经领取过 = 2817;
    public static final int EC_不是货品的购买者 = 2818;
    public static final int EC_货品已经领取过 = 2819;

    public MarketException(int errorCode, String message) {
        super(errorCode, message);
    }

}
