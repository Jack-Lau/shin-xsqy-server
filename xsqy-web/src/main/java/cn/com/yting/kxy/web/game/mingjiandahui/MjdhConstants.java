/*
 * Created 2018-12-13 13:09:35
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.time.LocalTime;

import cn.com.yting.kxy.core.validperiod.TimeOfDayValidPeriod;
import cn.com.yting.kxy.core.validperiod.ValidPeriod;

/**
 *
 * @author Azige
 */
public final class MjdhConstants {

    public static final ValidPeriod VALID_PERIOD_GAME = new TimeOfDayValidPeriod(LocalTime.of(12, 0), LocalTime.of(13, 0));
    public static final ValidPeriod VALID_PERIOD_GAME_EXTRA = new TimeOfDayValidPeriod(LocalTime.of(18, 0), LocalTime.of(19, 0));

    public static final int GRADE_青铜五 = 1;
    public static final int GRADE_白银五 = 11;
    public static final int GRADE_王者 = 86;

    public static final long DESIGNATED_AWARD_ID = 0;

    public MjdhConstants() {
    }
}
