/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class FishingException extends KxyWebException {

    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 3800;
    public static final int EC_INSUFFICIENT_POLE = 3801;
    public static final int EC_FISHING_NOT_FINISH = 3802;
    public static final int EC_CANNOT_FINISH_FISHING = 3803;
    public static final int EC_INSUFFICIENT_GOLD = 3804;
    public static final int EC_INSUFFICIENT_XS = 3805;
    public static final int EC_INSUFFICIENT_PLAYER_FC = 3806;

    public FishingException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static FishingException insufficientPlayerLevel() {
        return new FishingException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static FishingException insufficientPole() {
        return new FishingException(EC_INSUFFICIENT_POLE, "初级或高级钓竿数量不足");
    }

    public static FishingException fishingNotFinish() {
        return new FishingException(EC_FISHING_NOT_FINISH, "当前已经正在钓鱼");
    }

    public static FishingException cannotFinishFishing() {
        return new FishingException(EC_CANNOT_FINISH_FISHING, "尚未满足钓起这条鱼的条件");
    }

    public static FishingException insufficientGold() {
        return new FishingException(EC_INSUFFICIENT_GOLD, "持有的元宝不足");
    }

    public static FishingException insufficientXs() {
        return new FishingException(EC_INSUFFICIENT_XS, "持有的仙石不足");
    }

    public static FishingException insufficientPlayerFc() {
        return new FishingException(EC_INSUFFICIENT_PLAYER_FC, "角色战力不足");
    }

}
