/*
 * Created 2017-6-26 17:43:47
 */
package cn.com.yting.kxy.web.gift;

/**
 *
 * @author Azige
 */
public class GiftCode{

    private static final int[] serialCodeIndexes = {4, 7, 9, 11, 14};
    private static final int[] randomCodeIndexes = {3, 5, 6, 8, 10, 12, 13, 15};

    private final String prototypeCode;
    private final String serialCode;
    private final String randomCode;

    public GiftCode(String prototypeCode, String serialCode, String randomCode){
        this.prototypeCode = prototypeCode;
        this.serialCode = serialCode;
        this.randomCode = randomCode;
    }

    public static GiftCode parse(String code){
        char[] prototypeChars = new char[3];
        char[] serialChars = new char[5];
        char[] randomChars = new char[8];
        System.arraycopy(code.toCharArray(), 0, prototypeChars, 0, prototypeChars.length);
        for (int i = 0; i < serialCodeIndexes.length; i++){
            serialChars[i] = code.charAt(serialCodeIndexes[i]);
        }
        for (int i = 0; i < randomCodeIndexes.length; i++){
            randomChars[i] = code.charAt(randomCodeIndexes[i]);
        }
        return new GiftCode(new String(prototypeChars), new String(serialChars), new String(randomChars));
    }

    @Override
    public String toString(){
        char[] buffer = new char[16];
        System.arraycopy(prototypeCode.toCharArray(), 0, buffer, 0, prototypeCode.length());
        for (int i = 0; i < serialCodeIndexes.length; i++){
            buffer[serialCodeIndexes[i]] = serialCode.charAt(i);
        }
        for (int i = 0; i < randomCodeIndexes.length; i++){
            buffer[randomCodeIndexes[i]] = randomCode.charAt(i);
        }
        return new String(buffer);
    }

    public String getPrototypeCode(){
        return prototypeCode;
    }

    public String getSerialCode(){
        return serialCode;
    }

    public String getRandomCode(){
        return randomCode;
    }
}
