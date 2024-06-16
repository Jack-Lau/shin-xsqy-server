/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class ChanglefangConstants {

    public static final boolean AVAILABLE = true;

    public static final int PLAYER_LEVEL_REQUIRE = 50;

    public static final long MILLI_KC_COST_PER_SHARE = 500_000;

    public static final long ENERGY_COST_PER_KC = 1000;

    public static final long DAY_ENERGY_ADD_PER_SHARE = 475_000;

    public static final double DAY_ENERGY_ADD_RATE = 0.05;

    public static final List<Integer> 增加能量的消耗源 = Arrays.asList(
            2002,
            2011,
            2019,
            2022,
            2023,
            2025,
            2029);

}
