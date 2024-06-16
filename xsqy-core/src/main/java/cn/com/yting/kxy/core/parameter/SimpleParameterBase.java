/*
 * Created 2015-11-16 15:59:10
 */
package cn.com.yting.kxy.core.parameter;

/**
 * 简单的参数基元实现。这个类的对象是不可变的。
 *
 * @author Azige
 */
public class SimpleParameterBase implements ParameterBase{

    private final double base;
    private final double factor;

    /**
     * 构造一个值与{@link #ZERO 零值}相同的参数基元。
     */
    public SimpleParameterBase(){
        this(0, 0);
    }

    /**
     * 只用基数构造一个参数基元，因子为0.
     *
     * @param base 基数
     */
    public SimpleParameterBase(double base){
        this(base, 0);
    }

    /**
     * 用基数和因子构造一个参数基元。
     *
     * @param base 基数
     * @param factor 因子
     */
    public SimpleParameterBase(double base, double factor){
        this.base = base;
        this.factor = factor;
    }

    @Override
    public double getBase(){
        return base;
    }

    @Override
    public double getFactor(){
        return factor;
    }
}
