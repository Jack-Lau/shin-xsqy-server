/*
 * Created 2018-9-19 17:10:41
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class EquipmentSpeciallyEffectLoader extends XmlMapContainerResourceLoader<EquipmentSpeciallyEffect> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSpeciallyEffect.xml";
    }

    @Override
    public Class<EquipmentSpeciallyEffect> getSupportedClass() {
        return EquipmentSpeciallyEffect.class;
    }
}
