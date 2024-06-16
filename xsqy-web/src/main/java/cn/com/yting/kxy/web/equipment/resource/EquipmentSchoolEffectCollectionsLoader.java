/*
 * Created 2018-9-19 18:17:42
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class EquipmentSchoolEffectCollectionsLoader extends XmlMapContainerResourceLoader<EquipmentSchoolEffectCollections> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSchoolEffectCollections.xml";
    }

    @Override
    public Class<EquipmentSchoolEffectCollections> getSupportedClass() {
        return EquipmentSchoolEffectCollections.class;
    }
}
