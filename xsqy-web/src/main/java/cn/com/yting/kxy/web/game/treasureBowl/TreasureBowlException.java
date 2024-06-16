/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class TreasureBowlException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 4000;
    public static final int EC_INSUFFICIENT_CHANGLEFANG_SHARE = 4001;
    public static final int EC_INSUFFICIENT_CHANGLE_TOKEN = 4002;
    public static final int EC_NOT_STARTED_YET = 4003;
    public static final int EC_TODAY_TOKEN_REACH_LIMIT = 4004;
    public static final int EC_NO_AWARD = 4005;

    public TreasureBowlException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static TreasureBowlException insufficientPlayerLevel() {
        return new TreasureBowlException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static TreasureBowlException insufficientChanglefangShare() {
        return new TreasureBowlException(EC_INSUFFICIENT_CHANGLEFANG_SHARE, "角色持有坊票不足");
    }

    public static TreasureBowlException insufficientChangleToken() {
        return new TreasureBowlException(EC_INSUFFICIENT_CHANGLE_TOKEN, "角色持有贡牌不足");
    }

    public static TreasureBowlException notStartedYet() {
        return new TreasureBowlException(EC_NOT_STARTED_YET, "当期活动尚未开始");
    }

    public static TreasureBowlException todayTokenReachLimit() {
        return new TreasureBowlException(EC_TODAY_TOKEN_REACH_LIMIT, "今日使用贡牌已达上限");
    }

    public static TreasureBowlException noAward() {
        return new TreasureBowlException(EC_NO_AWARD, "当前没有可领取的奖励");
    }

}
