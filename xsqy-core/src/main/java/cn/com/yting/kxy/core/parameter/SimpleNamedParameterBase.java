/*
 * Created 2015-11-30 18:24:29
 */
package cn.com.yting.kxy.core.parameter;

/**
 * 简单命名参数基元的实现。此对象组合另一个参数基元以对其进行名命。
 *
 * @author Azige
 */
public class SimpleNamedParameterBase implements NamedParameterBase {

    private final String name;
    private final ParameterBase parameterBase;

    /**
     * 以名字和一个参数基元构造对象。
     *
     * @param name 名字
     * @param parameterBase 参数基元
     */
    public SimpleNamedParameterBase(String name, ParameterBase parameterBase) {
        this.name = name;
        this.parameterBase = parameterBase;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getBase() {
        return parameterBase.getBase();
    }

    @Override
    public double getFactor() {
        return parameterBase.getFactor();
    }

}
