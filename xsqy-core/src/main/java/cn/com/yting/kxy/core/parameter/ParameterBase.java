/*
 * Created 2015-11-20 15:05:03
 */
package cn.com.yting.kxy.core.parameter;

/**
 * 参数基元是一个由 {@link #getBase() 基数} 和 {@link #getFactor() 因子} 组成的二元组。 可以进行
 * {@link #plus 相加} 运算，可以 {@link #exportValue() 导出} 用于参数的最终值。
 *
 * @author Azige
 */
public interface ParameterBase {

    /**
     * <b>零值</b>，任何对象与此对象相加都会获得同样的值。
     */
    ParameterBase ZERO = new SimpleParameterBase(0, 0);

    /**
     * 获得参数基元的<b>基数</b>。
     *
     * @return 基数
     */
    double getBase();

    /**
     * 获得参数基元的<b>因子</b>。
     *
     * @return 因子
     */
    double getFactor();

    /**
     * 获得参数基元的<b>导出值</b>。 导出值 = 基数 * (1 + 因子)
     *
     * @return 导出值
     */
    default double exportValue() {
        return getBase() * (1 + getFactor());
    }

    /**
     * <b>相加</b> 运算，参数基元的相加运算是把两个对象的基数与因子分别相加。
     *
     * @param other 另一个操作数
     * @return 相加的结果
     */
    default ParameterBase plus(ParameterBase other) {
        return new SimpleParameterBase(getBase() + other.getBase(), getFactor() + other.getFactor());
    }

    default ParameterBase lazyPlus(ParameterBase other) {
        return new ParameterBase() {
            @Override
            public double getBase() {
                return ParameterBase.this.getBase() + other.getBase();
            }

            @Override
            public double getFactor() {
                return ParameterBase.this.getFactor() + other.getFactor();
            }
        };
    }

    /**
     * 用于 lambda 表达式的，因子始终为 0 的参数基元函数式接口。
     */
    @FunctionalInterface
    public interface BaseOnly extends ParameterBase {

        @Override
        default double getFactor() {
            return 0;
        }
    }
}
