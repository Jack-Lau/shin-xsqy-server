/*
 * Created 2015-10-23 12:13:48
 */
package cn.com.yting.kxy.core.random;

import java.util.Random;

/**
 * 随机对象提供器，基于线程提供同一个随机对象，便于在测试中设置随机数种子
 *
 * @author Azige
 */
public class RandomProvider {

    private static final ThreadLocal<Random> randomLocal = ThreadLocal.withInitial(Random::new);

    public static Random getRandom() {
        return randomLocal.get();
    }
}
