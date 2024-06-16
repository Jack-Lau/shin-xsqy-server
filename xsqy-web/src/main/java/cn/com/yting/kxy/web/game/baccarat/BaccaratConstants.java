/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

/**
 *
 * @author Darkholme
 */
public class BaccaratConstants {

    public static final int PLAYER_LEVEL_REQUIRE = 50;

    public static final long BET_LIMIT = 10000_000;

    public static final long BROADCAST_LOTTERY = 5000_000;

    public static final String BET_CRON = "0 * * * * *";

    public static final String LOTTERY_CRON = "30 * * * * *";

    public static final String BROADCAST_CRON = "50 * * * * *";

    public static enum Status {
        BET,
        WAIT
    }

}
