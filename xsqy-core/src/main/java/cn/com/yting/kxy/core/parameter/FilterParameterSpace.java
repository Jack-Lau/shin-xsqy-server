/*
 * Created 2018-10-26 13:00:17
 */
package cn.com.yting.kxy.core.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 在另一个参数空间上过滤出部分映射的参数空间
 *
 * @author Azige
 */
public class FilterParameterSpace implements ParameterSpace {

    private Set<String> filteredNames;
    private ParameterSpace delegate;

    public FilterParameterSpace(String filteredName, ParameterSpace delegate) {
        this(Collections.singleton(filteredName), delegate);
    }

    public FilterParameterSpace(Set<String> filteredNames, ParameterSpace delegate) {
        this.filteredNames = filteredNames;
        this.delegate = delegate;
    }

    @Override
    public ParameterBase getParameterBase(String name) {
        if (filteredNames.contains(name)) {
            return delegate.getParameterBase(name);
        } else {
            return ParameterBase.ZERO;
        }
    }

    @Override
    public Map<String, ? extends ParameterBase> toMap() {
        Map<String, ParameterBase> map = new HashMap<>();
        filteredNames.forEach(name -> map.put(name, delegate.getParameterBase(name)));
        return map;
    }
}
