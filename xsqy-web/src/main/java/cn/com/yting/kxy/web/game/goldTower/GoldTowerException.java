/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class GoldTowerException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 1200;
    public static final int EC_INSUFFICIENT_CHALLENGE_COUNT = 1201;
    public static final int EC_INSUFFICIENT_CURRENCY = 1202;
    public static final int EC_NOT_IN_CHALLENGE = 1203;
    public static final int EC_CHALLENGE_SUCCEED = 1204;
    public static final int EC_NOT_AT_FLOOR_ZERO = 1205;
    public static final int EC_INSUFFICIENT_MAX_FINISH_FLOOR = 1206;
    public static final int EC_CANNOT_FAST_UP = 1207;
    public static final int EC_CANNOT_TAKE_WIPE_OUT_AWARD = 1208;

    public GoldTowerException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static GoldTowerException insufficientPlayerLevel() {
        return new GoldTowerException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static GoldTowerException insufficientChallengeCount() {
        return new GoldTowerException(EC_INSUFFICIENT_CHALLENGE_COUNT, "挑战次数不足");
    }

    public static GoldTowerException insufficientCurrency() {
        return new GoldTowerException(EC_INSUFFICIENT_CURRENCY, "需要的货币数量不足");
    }

    public static GoldTowerException notInChallenge() {
        return new GoldTowerException(EC_NOT_IN_CHALLENGE, "当前不在挑战中");
    }

    public static GoldTowerException challengeSucceed() {
        return new GoldTowerException(EC_CHALLENGE_SUCCEED, "当前房间的挑战已通过");
    }

    public static GoldTowerException notAtFloorZero() {
        return new GoldTowerException(EC_NOT_AT_FLOOR_ZERO, "当前不在第0层");
    }

    public static GoldTowerException insufficientMaxFinishFloor() {
        return new GoldTowerException(EC_INSUFFICIENT_MAX_FINISH_FLOOR, "历史最高通过楼层数不足");
    }

    public static GoldTowerException cannotFastUp() {
        return new GoldTowerException(EC_CANNOT_FAST_UP, "不满足快速传送的条件");
    }

    public static GoldTowerException cannotTakeWipeOutAward() {
        return new GoldTowerException(EC_CANNOT_TAKE_WIPE_OUT_AWARD, "不满足领取扫荡奖励的条件");
    }

}
