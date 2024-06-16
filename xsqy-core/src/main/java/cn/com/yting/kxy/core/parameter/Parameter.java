/*
 * Created 2015-11-5 16:04:28
 */
package cn.com.yting.kxy.core.parameter;

import java.io.Serializable;

/**
 * 参数是一个命名值，包含了名字与值。
 *
 * @author Azige
 */
public interface Parameter extends Comparable<Parameter>, Serializable {

    /**
     * 获得参数的名字
     *
     * @return 名字
     */
    String getName();

    /**
     * 获得参数的值
     *
     * @return 值
     */
    double getValue();

    /**
     * 比较参数的值。
     *
     * @param another 另一个参数
     * @return 参数的值的比较结果
     */
    @Override
    default int compareTo(Parameter another) {
        return (int) (this.getValue() - another.getValue());
    }
}
