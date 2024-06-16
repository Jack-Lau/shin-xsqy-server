/*
 * Created 2018-8-18 10:56:28
 */
package cn.com.yting.kxy.battle.affect;

import java.util.Random;

import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class AffectUtils {

    public static double rand(double lowerLimit, double upperLimit) {
        Random random = RandomProvider.getRandom();
        return lowerLimit + random.nextDouble() * (upperLimit - lowerLimit);
    }
}
