/*
 * Created 2018-10-13 11:56:58
 */
package cn.com.yting.kxy.core.parameter;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * 只包含单一映射的参数空间
 *
 * @author Azige
 */
public class SingleElementParameterSpace extends SimpleNamedParameterBase implements ParameterSpace {

    public SingleElementParameterSpace(String name, ParameterBase parameterBase) {
        super(name, parameterBase);
    }

    @Override
    public ParameterBase getParameterBase(String name) {
        if (getName().equals(name)) {
            return this;
        } else {
            return ParameterBase.ZERO;
        }
    }

    @Override
    public Map<String, ? extends ParameterBase> toMap() {
        return ImmutableMap.of(getName(), this);
    }
}
