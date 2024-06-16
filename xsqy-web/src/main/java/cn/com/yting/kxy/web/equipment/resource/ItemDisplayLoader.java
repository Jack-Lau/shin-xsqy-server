/*
 * Created 2018-10-27 18:13:08
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class ItemDisplayLoader extends XmlMapContainerResourceLoader<ItemDisplay> {

    private Map<Long, ItemDisplay> definitionIdMap;

    @Override
    protected void afterReload(ResourceContext context) {
        definitionIdMap = getMap().values().stream()
            .collect(Collectors.toMap(it -> it.getModelId(), Function.identity(), (a, b) -> a));
    }

    public ItemDisplay getByDefinitionId(long definitionId) {
        return definitionIdMap.get(definitionId);
    }

    @Override
    public String getDefaultResourceName() {
        return "sItemDisplay.xml";
    }

    @Override
    public Class<ItemDisplay> getSupportedClass() {
        return ItemDisplay.class;
    }
}
