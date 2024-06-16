/*
 * Created 2015-11-16 16:28:00
 */
package cn.com.yting.kxy.core.parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单参数空间实现。使用 {@link HashMap} 作为映射容器。
 *
 * @author Azige
 */
public class SimpleParameterSpace implements RootParameterSpace {

    private Map<String, NamedParameterBase> parameterBaseMap;

    public SimpleParameterSpace() {
        parameterBaseMap = new HashMap<>();
    }

    /**
     * 用一个名字与参数基元的 Map 构造一个对象。
     *
     * @param map 包含了映射的对象
     */
    public SimpleParameterSpace(Map<String, ? extends ParameterBase> map) {
        parameterBaseMap = new HashMap<>();
        map.forEach((key, value) -> parameterBaseMap.put(key, new SimpleNamedParameterBase(key, value)));
    }

    public void addParameterBase(String name, ParameterBase parameterBase) {
        parameterBaseMap.put(name, new SimpleNamedParameterBase(name, parameterBase));
    }

    @Override
    public NamedParameterBase getParameterBase(String name) {
        return parameterBaseMap.get(name);
    }

    @Override
    public Map<String, NamedParameterBase> toMap() {
        return new HashMap<>(parameterBaseMap);
    }
}
