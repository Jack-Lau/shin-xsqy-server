/*
 * Created 2018-7-26 16:36:28
 */
package cn.com.yting.kxy.core.validperiod;

import java.time.Duration;
import java.time.Instant;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class AbsoluteValidPeriod implements ValidPeriod {

    private Instant startTime;
    private Instant endTime;

    @Override
    public boolean isValid(Instant instant) {
        return instant.compareTo(startTime) >= 0 && instant.compareTo(endTime) < 0;
    }

    @Override
    public long getTimeToExpire(Instant instant) {
        return instant.toEpochMilli() - endTime.toEpochMilli();
    }
}
