/*
 * Created 2015-10-15 14:36:19
 */
package cn.com.yting.kxy.core.parameter;

/**
 * 简单的参数实现。
 *
 * @author Azige
 */
public class SimpleParameter implements Parameter{

    private double value;
    private String name;

    public SimpleParameter(){
    }

    public SimpleParameter(String name, double value){
        this.name = name;
        this.value = value;
    }

    @Override
    public double getValue(){
        return value;
    }

    /**
     * 设置参数的值
     *
     * @param value
     */
    protected void setValue(double value){
        this.value = value;
    }

    @Override
    public String getName(){
        return name;
    }

    /**
     * 设置参数的名字
     *
     * @param name
     */
    protected void setName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "SimpleParameter{" + "value=" + value + ", name=" + name + '}';
    }
}
