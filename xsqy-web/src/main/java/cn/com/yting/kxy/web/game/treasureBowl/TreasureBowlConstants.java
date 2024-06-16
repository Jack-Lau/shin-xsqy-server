/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.core.validperiod.TimeOfDayValidPeriod;
import cn.com.yting.kxy.core.validperiod.ValidPeriod;
import java.time.LocalTime;

/**
 *
 * @author Administrator
 */
public class TreasureBowlConstants {

    public static final ValidPeriod VALID_PERIOD_GAME = new TimeOfDayValidPeriod(LocalTime.of(10, 0), LocalTime.of(22, 0));

    public static final int PLAYER_LEVEL_REQUIRE = 50;

    public static long CHANGLEFANG_SHARE_REQUIRE = 30;

    public static long MAX_TOKEN_USE_PER_DAY = 100;

    public static long BIG_FACTOR_BROADCAST = 3200066;

}
