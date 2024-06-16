/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class BrawlException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 2400;
    public static final int EC_BRAWL_STATUS_INCORRECT = 2401;
    public static final int EC_INSUFFICIENT_RESET_COUNT = 2402;
    public static final int EC_INSUFFICIENT_BRAWL_COUNT = 2403;

    public BrawlException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static BrawlException insufficientPlayerLevel() {
        return new BrawlException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static BrawlException brawlStatusIncorrect() {
        return new BrawlException(EC_BRAWL_STATUS_INCORRECT, "挑战状态不对");
    }

    public static BrawlException insufficientResetCount() {
        return new BrawlException(EC_INSUFFICIENT_RESET_COUNT, "重置次数不足");
    }

    public static BrawlException insufficientBrawlCount() {
        return new BrawlException(EC_INSUFFICIENT_BRAWL_COUNT, "挑战次数不足");
    }

}
