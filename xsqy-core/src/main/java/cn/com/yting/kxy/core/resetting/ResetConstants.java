/*
 * Created 2016-11-29 17:02:59
 */
package cn.com.yting.kxy.core.resetting;

/**
 *
 * @author Azige
 */
public final class ResetConstants {

    public static final String CRON_HOURLY = "0 0 * * * *";
    public static final String CRON_DAILY = "0 0 0 * * *";
    public static final String CRON_WEEKLY = "0 0 0 * * MON";
    public static final String CRON_MONTHLY = "0 0 0 1 * *";

    private ResetConstants() {
    }
}
