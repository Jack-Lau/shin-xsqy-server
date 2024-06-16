/*
 * Created 2018-9-20 16:35:14
 */
package cn.com.yting.kxy.web.recycling.resource;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class WasteRecoveryLoader extends XmlMapContainerResourceLoader<WasteRecovery> {

    private RandomSelector<WasteRecovery> equipmentSelector;
    private RandomSelector<WasteRecovery> petSelector;

    @Override
    protected void afterReload(ResourceContext context) {
        equipmentSelector = createSelector(1);
        petSelector = createSelector(2);
    }

    private RandomSelector<WasteRecovery> createSelector(int type) {
        RandomSelectorBuilder<WasteRecovery> builder = RandomSelector.builder();
        getAll().values().stream()
            .filter(it -> it.getType() == type)
            .forEach(it -> builder.add(it, it.getWeight()));
        return builder.build(RandomSelectType.DEPENDENT);
    }

    public RandomSelector<WasteRecovery> getEquipmentSelector() {
        return equipmentSelector;
    }

    public RandomSelector<WasteRecovery> getPetSelector() {
        return petSelector;
    }

    @Override
    public String getDefaultResourceName() {
        return "sWasteRecovery.xml";
    }

    @Override
    public Class<WasteRecovery> getSupportedClass() {
        return WasteRecovery.class;
    }
}
