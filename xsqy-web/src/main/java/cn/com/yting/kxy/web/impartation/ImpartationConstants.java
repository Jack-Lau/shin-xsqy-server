/*
 * Created 2018-11-20 15:42:50
 */
package cn.com.yting.kxy.web.impartation;

import java.time.Duration;
import java.time.Period;

/**
 *
 * @author Azige
 */
public class ImpartationConstants {

    public static final Duration DURATION_DISCIPLE_LIMIT = Duration.ofDays(7);
    public static final Duration DURATION_MASTER_LIMIT = Duration.ofDays(7);
    public static final Duration DURATION_DISCIPLINE = Duration.ofDays(7);
    
    public static final long DAYS_活跃点_AWARD_AVAILABLE_DISCIPLE = 7;
    public static final long DAYS_活跃点_AWARD_AVAILABLE_MASTER = 7;

    public static final long LEVEL_MASTER_REQUIREMENT = 70;
    public static final long FC_MASTER_REQUIREMENT = 40000;

    public static final int DISCIPLES_COUNT_LIMIT = 6;

    public static final double RATE_活跃点_AWARD_ON_CONFIRMATION_DISCIPLE = 0.3;
    public static final double RATE_活跃点_AWARD_ON_CONFIRMATION_MASTER = 0.3;

    public static final long MAIL_ID_CONFIRMATION = 45;

    public static final long BROADCAST_ID_CONFIRMATION = 3200030;

    public static final Period PERIOD_LAST_LOGIN_TO_DELETE = Period.ofDays(3);
}
