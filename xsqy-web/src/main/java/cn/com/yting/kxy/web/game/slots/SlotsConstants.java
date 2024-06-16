/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class SlotsConstants {

    public static final boolean AVAILABLE = true;

    public static final int PLAYER_LEVEL_REQUIRE = 50;

    public static final int MAX_FRIEND_BIG_PRIZE_COUNT = 20;

    public static final int MAX_LIKE_SEND_COUNT = 20;

    public static final int MAX_LIKE_RECEIVE_COUNT = 40;

    public static final long LIKE_SEND_AWARD_ID = 4030;

    public static final long LIKE_RECEIVE_AWARD_ID = 4031;
    
    public static final long MAIL_END_ID = 47;

    public static final List<Integer> DEFAULT_LOCKS = Arrays.asList(0, 0, 0, 0);

    public static final List<Integer> DEFAULT_SLOTS = Arrays.asList(0, 0, 0, 0);

    public static final List<Long> PULL_MILLI_KC_COST = Arrays.asList(25_000L, 20_000L, 40_000L, 225_000L);

}
