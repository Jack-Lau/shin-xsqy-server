/*
 * Created 2018-9-19 17:49:43
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class EquipmentSpeciallyEffectCollectionsLoader extends XmlMapContainerResourceLoader<EquipmentSpeciallyEffectCollections> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSpeciallyEffectCollections.xml";
    }

    @Override
    public Class<EquipmentSpeciallyEffectCollections> getSupportedClass() {
        return EquipmentSpeciallyEffectCollections.class;
    }

}
