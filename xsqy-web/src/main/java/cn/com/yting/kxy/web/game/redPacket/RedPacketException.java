/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class RedPacketException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 3900;
    public static final int EC_NO_AWARD = 3901;
    public static final int EC_RED_PACKET_FINISHED = 3902;
    public static final int EC_INSUFFICIENT_XS = 3903;
    public static final int EC_DUPLICATE_OPEN = 3904;

    public RedPacketException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static RedPacketException insufficientPlayerLevel() {
        return new RedPacketException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static RedPacketException noAward() {
        return new RedPacketException(EC_NO_AWARD, "没有可领取的奖励");
    }

    public static RedPacketException redPacketFinished() {
        return new RedPacketException(EC_RED_PACKET_FINISHED, "红包已被抢完");
    }

    public static RedPacketException insufficientXS() {
        return new RedPacketException(EC_INSUFFICIENT_XS, "仙石不足");
    }

    public static RedPacketException duplicateOpen() {
        return new RedPacketException(EC_DUPLICATE_OPEN, "不能重复抢一个红包");
    }

}
