/*
 * Created 2018-8-10 13:18:05
 */
package cn.com.yting.kxy.core.parameter;

import java.util.HashMap;
import java.util.Map;

import cn.com.yting.kxy.core.parameter.ParameterBase.BaseOnly;

/**
 *
 * @author Azige
 */
public class ParameterSpaceBuilder {

    private Map<String, ParameterBase> parameterBaseMap = new HashMap<>();

    public ParameterSpaceBuilder simple(String name, double base) {
        return add(name, new SimpleParameterBase(base));
    }

    public ParameterSpaceBuilder simple(String name, double base, double factor) {
        return add(name, new SimpleParameterBase(base, factor));
    }

    public ParameterSpaceBuilder transform(String name, BaseOnly parameterBase) {
        return add(name, parameterBase);
    }

    public ParameterSpaceBuilder add(String name, ParameterBase parameterBase) {
        parameterBaseMap.merge(name, parameterBase, ParameterBase::lazyPlus);
        return this;
    }

    public ParameterSpace build() {
        return new SimpleParameterSpace(parameterBaseMap);
    }
}
