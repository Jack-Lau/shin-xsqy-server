/*
 * Created 2018-7-26 16:23:28
 */
package cn.com.yting.kxy.core.validperiod;

import java.time.Instant;

/**
 * 描述一段有效期的类型
 *
 * @author Azige
 */
public interface ValidPeriod {

    /**
     * 给定的时间是否在有效期内
     *
     * @param instant
     * @return
     */
    public boolean isValid(Instant instant);

    /**
     * 获得自给定的时间至有效期结束的时间，如果给定的时间不在有效期内，
     * 则返回为负值。单位为毫秒
     *
     * @param instant
     * @return
     */
    public long getTimeToExpire(Instant instant);
}
