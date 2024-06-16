/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class IdleMineException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 1700;
    public static final int EC_INSUFFICIENT_CURRENCY = 1701;
    public static final int EC_INSUFFICIENT_MINE_QUEUE_COUNT = 1702;
    public static final int EC_EMPTY_INDEX = 1703;
    public static final int EC_EMPTY_REWARD = 1704;
    public static final int EC_MINE_QUEUE_COUNT_MAX = 1705;
    public static final int EC_INSUFFICIENT_MAP_PLAYER_LEVEL = 1706;

    public IdleMineException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static IdleMineException insufficientPlayerLevel() {
        return new IdleMineException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static IdleMineException insufficientCurrency() {
        return new IdleMineException(EC_INSUFFICIENT_CURRENCY, "拥有的货币不足");
    }

    public static IdleMineException insufficientMineQueueCount() {
        return new IdleMineException(EC_INSUFFICIENT_MINE_QUEUE_COUNT, "空闲的经商位不足");
    }

    public static IdleMineException emptyIndex() {
        return new IdleMineException(EC_EMPTY_INDEX, "该经商位上没有商队");
    }

    public static IdleMineException emptyReward() {
        return new IdleMineException(EC_EMPTY_REWARD, "储物箱为空");
    }

    public static IdleMineException mineQueueCountMax() {
        return new IdleMineException(EC_MINE_QUEUE_COUNT_MAX, "经商位已达上限");
    }

    public static IdleMineException insufficientMapPlayerLevel() {
        return new IdleMineException(EC_INSUFFICIENT_MAP_PLAYER_LEVEL, "角色等级未达探索地点的等级要求");
    }

}
