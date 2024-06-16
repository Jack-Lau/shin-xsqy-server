/*
 * Created 2018-6-30 18:17:56
 */
package cn.com.yting.kxy.core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

import cn.com.yting.kxy.core.util.TimeUtils;

/**
 * 获取时间用的组件，用于在测试中假设时间
 *
 * @author Azige
 */
public class TimeProvider {

    private static final Supplier<Long> SYSTEM_TIME_PROVIDER = System::currentTimeMillis;
    private Supplier<Long> timeSupplier = SYSTEM_TIME_PROVIDER;

    public long currentTime() {
        return timeSupplier.get();
    }

    public Instant currentInstant() {
        return Instant.ofEpochMilli(timeSupplier.get());
    }

    public OffsetDateTime currentOffsetDateTime() {
        return TimeUtils.toOffsetTime(timeSupplier.get());
    }

    public LocalDate today() {
        return currentOffsetDateTime().toLocalDate();
    }

    public LocalDate yesterday() {
        return today().minusDays(1);
    }

    public void resetToSystemTime() {
        this.timeSupplier = SYSTEM_TIME_PROVIDER;
    }

    public void setMockedTime(long mockedTime) {
        this.timeSupplier = () -> mockedTime;
    }

    public void setTimeOffset(long offset) {
        this.timeSupplier = () -> System.currentTimeMillis() + offset;
    }

    public void setTimeSupplier(Supplier<Long> timeSupplier) {
        this.timeSupplier = timeSupplier;
    }
}
