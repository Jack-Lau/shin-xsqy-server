/*
 * Created 2018-9-15 13:13:00
 */
package cn.com.yting.kxy.web.currency.kuaibi;

/**
 * 块币的基准单位是毫块币，此类型用于基准单位与其它单位的转换
 *
 * @author Azige
 */
public final class KuaibiUnits {

    public static long fromKuaibi(long value) {
        return value * 1000;
    }

    public static long toKuaibi(long value) {
        return value / 1000;
    }

    public static long truncateAtKuaibi(long value) {
        return value / 1000 * 1000;
    }

    private KuaibiUnits() {
    }
}
