/*
 * Created 2015-10-10 13:40:00
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.SimpleParameter;

/**
 * 范围参数类。参数的值必须限定在范围内。
 *
 * @author Azige
 */
public class RangedParameter extends SimpleParameter{

    private Parameter upperLimit;
    private Parameter lowerLimit;

    /**
     * 只用当前值构造对象，范围为 0 至 value。
     *
     * @param name  名字
     * @param value 当前值
     */
    public RangedParameter(String name, double value){
        this(name, value, 0, value);
    }

    /**
     * 指定当前值与上下限构造对象
     *
     * @param name       名字
     * @param value      当前值
     * @param lowerLimit 上限
     * @param upperLimit 下限
     */
    public RangedParameter(String name, double value, double lowerLimit, double upperLimit){
        super(name, value);
        this.upperLimit = new SimpleParameter(name + " upperLimit", upperLimit);
        this.lowerLimit = new SimpleParameter(name + " lowerLimit", lowerLimit);
    }

    public Parameter getUpperLimit(){
        return upperLimit;
    }

    public Parameter getLowerLimit(){
        return lowerLimit;
    }

    /**
     * 设置参数的值，若超出上下限，则会引发异常。
     *
     * @param value
     * @throws IllegalArgumentException 如果给定的值超出上下限。
     */
    @Override
    public void setValue(double value){
        if (value < lowerLimit.getValue() || value > upperLimit.getValue()){
            throw new IllegalArgumentException("值超出范围：" + String.valueOf(value));
        }
        super.setValue(value);
    }

    public double getRate(){
        return (getValue() - lowerLimit.getValue()) / (upperLimit.getValue() - lowerLimit.getValue());
    }

    public void setRate(double rate){
        if (rate < 0 || rate > 1){
            throw new IllegalArgumentException("rate必须在0.0到1.0之间");
        }
        setValue(lowerLimit.getValue() + rate * (upperLimit.getValue() - lowerLimit.getValue()));
    }

    /**
     * 给此参数加上一个偏移，偏移后的值永远不会超过上限和下限。
     *
     * @param offset 偏移量
     * @return 实际偏移的值
     */
    public double shift(double offset){
        double finalValue = setValueInBound(getValue() + offset);
        offset = finalValue - getValue();
        return offset;
    }

    /**
     * 设置参数值，如果超过了限制，则设置为对应的限制值。
     *
     * @param value 要设置的值
     * @return 设置后的实际的值
     */
    public double setValueInBound(double value){
        if (value < lowerLimit.getValue()){
            value = lowerLimit.getValue();
        }else if (value > upperLimit.getValue()){
            value = upperLimit.getValue();
        }
        super.setValue(value);
        return value;
    }

    @Override
    public String toString(){
        return "Parameter{" + "name=" + getName() + ", value=" + getValue() + ", lowerLimit=" + lowerLimit + ", upperLimit=" + upperLimit + '}';
    }
}
