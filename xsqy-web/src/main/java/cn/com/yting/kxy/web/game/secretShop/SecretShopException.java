/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class SecretShopException extends KxyWebException {

    public static final int EC_INSUFFICIENT_CURRENCY = 2000;
    public static final int EC_HAVE_NOT_TAKEN_PRIZE = 2001;
    public static final int EC_NOT_HAVE_NOT_TAKEN_PRIZE = 2002;
    public static final int EC_INSUFFICIENT_KC_PACK = 2003;
    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 2004;
    public static final int EC_QUEST_NOT_FINISH = 2005;
    public static final int EC_KC_PACK_EXCHANGE_COUNT_REACH_LIMIT = 2006;
    public static final int EC_INSUFFICIENT_PLAYER_FC = 2007;

    public SecretShopException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static SecretShopException insufficientCurrency() {
        return new SecretShopException(EC_INSUFFICIENT_CURRENCY, "玉石不足");
    }

    public static SecretShopException haveNotTakenPrize() {
        return new SecretShopException(EC_HAVE_NOT_TAKEN_PRIZE, "还有未领取的奖励");
    }

    public static SecretShopException notHaveNotTakenPrize() {
        return new SecretShopException(EC_NOT_HAVE_NOT_TAKEN_PRIZE, "没有可领取的奖励");
    }

    public static SecretShopException insufficientKCPack() {
        return new SecretShopException(EC_INSUFFICIENT_KC_PACK, "块币补给包已经兑换完了");
    }

    public static SecretShopException insufficientPlayerLevel() {
        return new SecretShopException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static SecretShopException questNotFinish() {
        return new SecretShopException(EC_QUEST_NOT_FINISH, "前置任务未完成");
    }

    public static SecretShopException kcPackExchangeCountReachLimit() {
        return new SecretShopException(EC_KC_PACK_EXCHANGE_COUNT_REACH_LIMIT, "已达个人兑换上限");
    }

    public static SecretShopException insufficientPlayerFc() {
        return new SecretShopException(EC_INSUFFICIENT_PLAYER_FC, "角色战力不足");
    }

}
