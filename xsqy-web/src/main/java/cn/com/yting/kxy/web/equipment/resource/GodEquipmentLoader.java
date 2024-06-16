/*
 * Created 2018-11-13 18:11:35
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GodEquipmentLoader extends XmlMapContainerResourceLoader<GodEquipment> {

    @Override
    public String getDefaultResourceName() {
        return "sGodEquipment.xml";
    }

    @Override
    public Class<GodEquipment> getSupportedClass() {
        return GodEquipment.class;
    }
}
