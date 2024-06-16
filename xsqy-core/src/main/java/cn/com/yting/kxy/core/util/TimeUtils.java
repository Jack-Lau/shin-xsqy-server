/*
 * Created 2018-7-10 12:16:16
 */
package cn.com.yting.kxy.core.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

import cn.com.yting.kxy.core.KxyConstants;

/**
 *
 * @author Azige
 */
public final class TimeUtils {

    public static OffsetDateTime toOffsetTime(long epochTime) {
        return Instant.ofEpochMilli(epochTime).atOffset(KxyConstants.KXY_TIME_OFFSET);
    }

    public static OffsetDateTime toOffsetTime(Date date) {
        return date.toInstant().atOffset(KxyConstants.KXY_TIME_OFFSET);
    }

    private TimeUtils() {
    }
}
