/*
 * Created 2018-8-28 15:45:48
 */
package cn.com.yting.kxy.web.ethereum;

/**
 *
 * @author Azige
 */
public final class EthereumExchangeConstants {

    public static final long WITHDRAW_MIN_AMOUNT = 100_000;
    public static final long DEPOSIT_MIN_AMOUNT = 1_000;

    public static final long LIMIT_PER_ACCOUNT_DAILY_WITHDRAW = 10000_000;

    public static final long LIMIT_GLOBAL_DAILY_WITHDRAW_WARN = 300000_000;
    public static final long LIMIT_GLOBAL_DAILY_WITHDRAW = 400000_000;

    private EthereumExchangeConstants() {
    }

}
