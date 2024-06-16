/*
 * Created 2018-9-17 16:59:05
 */
package cn.com.yting.kxy.core.random.resource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.random.NormalRandomGenerator;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class StochasticModelLoader extends XmlMapContainerResourceLoader<StochasticModel> {

    private Map<Integer, NormalRandomGenerator> nrgMap;

    @Override
    protected void afterReload(ResourceContext context) {
        nrgMap = new HashMap<>();
        nrgMap.put(1, new NormalRandomGenerator(createModelList(1)));
        nrgMap.put(2, new NormalRandomGenerator(createModelList(2)));
    }

    private List<Double> createModelList(int index) {
        return getMap().entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey()))
            .map(entry -> entry.getValue().getValueByIndex(index))
            .collect(Collectors.toList());
    }

    public NormalRandomGenerator getNormalRandomGenerator(int index) {
        if (!nrgMap.containsKey(index)) {
            throw new NoSuchElementException("不可用的索引：" + index);
        }
        return nrgMap.get(index);
    }

    @Override
    public String getDefaultResourceName() {
        return "sStochasticModel.xml";
    }

    @Override
    public Class<StochasticModel> getSupportedClass() {
        return StochasticModel.class;
    }
}
