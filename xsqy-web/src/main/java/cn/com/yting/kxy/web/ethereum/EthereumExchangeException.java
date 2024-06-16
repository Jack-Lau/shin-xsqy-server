/*
 * Created 2018-8-28 15:36:36
 */
package cn.com.yting.kxy.web.ethereum;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class EthereumExchangeException extends KxyWebException {

    public static final int EC_WITHDRAW_HIT_LIMIT = 2600;
    public static final int EC_WITHDRAW_HIT_GLOBAL_LIMIT = 2601;
    public static final int EC_提现块币数低于最低提现要求 = 2602;
    public static final int EC_提现所需等级不足 = 2603;
    public static final int EC_提现所需战斗力不足 = 2604;

    public EthereumExchangeException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static EthereumExchangeException withdrawHitLimit() {
        return new EthereumExchangeException(EC_WITHDRAW_HIT_LIMIT, "今日提现已达到上限");
    }

    public static EthereumExchangeException withdrawHitGlobalLimit() {
        return new EthereumExchangeException(EC_WITHDRAW_HIT_LIMIT, "今日提现已达到总量上限");
    }

    public static EthereumExchangeException 提现块币数低于最低提现要求() {
        return new EthereumExchangeException(EC_提现块币数低于最低提现要求, "提现块币数低于最低提现要求");
    }

    public static EthereumExchangeException 提现所需等级不足() {
        return new EthereumExchangeException(EC_提现所需等级不足, "提现所需等级不足");
    }

    public static EthereumExchangeException 提现所需战斗力不足() {
        return new EthereumExchangeException(EC_提现所需战斗力不足, "提现所需战斗力不足");
    }

}
