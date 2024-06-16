/*
 * Created 2017-6-22 17:40:24
 */
package cn.com.yting.kxy.web.gift;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Azige
 */
public class GiftCodeGenerator{

    /**
     * 序列码的最大值，即5位36进制数的最大值，即36的5次幂。
     */
    public static final int MAX_SERIAL_CODE = 60466176;
    /**
     * 一半的随机码的最大值，即4位36进制数的最大值，即36的4次幂。
     * 36的8次幂超过了int上限，故分成两段
     */
    public static final int MAX_HALF_RANDOM_CODE = 1679616;

    private final String prototypeCode;
    private final int serialCodeBegin;
    private final Random random = new Random();

    private int count;

    public GiftCodeGenerator(String prototypeCode, int serialCodeBegin){
        this(prototypeCode, serialCodeBegin, 0);
    }

    public GiftCodeGenerator(String prototypeCode, int serialCodeBegin, int count){
        if (prototypeCode == null || !prototypeCode.matches("[0-9A-Z]{3}")){
            throw new IllegalArgumentException("序列码必须为3位36进制数，当前参数=" + prototypeCode);
        }
        if (serialCodeBegin < 0 || serialCodeBegin > MAX_SERIAL_CODE){
            throw new IllegalArgumentException("序列码开始数必须为非负的，小于" + MAX_SERIAL_CODE + "的整数，当前参数=" + serialCodeBegin);
        }
        this.prototypeCode = prototypeCode;
        this.serialCodeBegin = serialCodeBegin;
        this.count = count;
    }

    public GiftCode generate(){
        int serialNumber = (serialCodeBegin + count) % MAX_SERIAL_CODE;
        count++;
        String serialCode = fillPrefixZero(Integer.toString(serialNumber, 36), 5).toUpperCase();
        String randCodeLow = fillPrefixZero(Integer.toString(random.nextInt(MAX_HALF_RANDOM_CODE), 36), 4).toUpperCase();
        String randCodeHigh = fillPrefixZero(Integer.toString(random.nextInt(MAX_HALF_RANDOM_CODE), 36), 4).toUpperCase();
        return new GiftCode(prototypeCode, serialCode, randCodeHigh + randCodeLow);
    }

    private String fillPrefixZero(String str, int length){
        if (str.length() >= length){
            return str;
        }
        char[] buffer = new char[length];
        Arrays.fill(buffer, 0, length - str.length(), '0');
        System.arraycopy(str.toCharArray(), 0, buffer, length - str.length(), str.length());
        return new String(buffer);
    }

    public String getPrototypeCode(){
        return prototypeCode;
    }

    public int getSerialCodeBegin(){
        return serialCodeBegin;
    }

    public int getCount(){
        return count;
    }
}
