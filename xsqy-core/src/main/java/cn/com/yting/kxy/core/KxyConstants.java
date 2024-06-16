/*
 * Created 2018-7-10 12:07:32
 */
package cn.com.yting.kxy.core;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Azige
 */
public final class KxyConstants {

    public static final ZoneOffset KXY_TIME_OFFSET = ZoneOffset.of("+8");
    public static final DateTimeFormatter KXY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(KXY_TIME_OFFSET);

    public static final long CURRENCY_GOLD_ID = 150L;
}
