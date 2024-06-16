/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.cultivation;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class CultivationException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 3600;
    public static final int EC_INSUFFICIENT_CURRENCY = 3601;
    public static final int EC_CULTIVATION_LEVEL_REACH_LIMIT = 3602;

    public CultivationException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static CultivationException insufficientPlayerLevel() {
        return new CultivationException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static CultivationException insufficientCurrency() {
        return new CultivationException(EC_INSUFFICIENT_CURRENCY, "金坷垃不足");
    }

    public static CultivationException cultivationLevelReachLimit() {
        return new CultivationException(EC_CULTIVATION_LEVEL_REACH_LIMIT, "修炼等级已达上限");
    }

}
