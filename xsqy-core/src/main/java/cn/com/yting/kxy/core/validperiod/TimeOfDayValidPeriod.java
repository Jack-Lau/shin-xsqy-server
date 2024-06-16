/*
 * Created 2018-12-13 12:31:45
 */
package cn.com.yting.kxy.core.validperiod;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

import cn.com.yting.kxy.core.KxyConstants;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class TimeOfDayValidPeriod implements ValidPeriod {

    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public boolean isValid(Instant instant) {
        LocalTime time = instant.atOffset(KxyConstants.KXY_TIME_OFFSET).toLocalTime();
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    @Override
    public long getTimeToExpire(Instant instant) {
        LocalTime time = instant.atOffset(KxyConstants.KXY_TIME_OFFSET).toLocalTime();
        if (!time.isBefore(startTime) && time.isBefore(endTime)) {
            return Duration.between(time, endTime).getSeconds();
        } else {
            return -1;
        }
    }

}
