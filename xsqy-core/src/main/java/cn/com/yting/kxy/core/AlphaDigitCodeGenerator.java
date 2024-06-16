/*
 * Created 2018-7-9 15:43:47
 */
package cn.com.yting.kxy.core;

import java.util.Random;

/**
 * 数字和字母混合码的生成器
 *
 * @author Azige
 */
public class AlphaDigitCodeGenerator {

    private final int digitCount;
    private final int radix;
    private final Random random;

    private int limit;

    public AlphaDigitCodeGenerator(int digitCount) {
        this(digitCount, Character.MAX_RADIX, new Random());
    }

    public AlphaDigitCodeGenerator(int digitCount, int radix, Random random) {
        this.digitCount = digitCount;
        this.radix = radix;
        this.random = random;

        this.limit = (int) Math.pow(radix, digitCount);
    }

    public String generateCode() {
        int randomNumber = random.nextInt(limit);
        StringBuilder sb = new StringBuilder(Integer.toString(randomNumber, radix));
        while (sb.length() < digitCount) {
            sb.insert(0, 'A');
        }
        return sb.toString().toUpperCase().replaceAll("[0O1I]", "Z");
    }
}
