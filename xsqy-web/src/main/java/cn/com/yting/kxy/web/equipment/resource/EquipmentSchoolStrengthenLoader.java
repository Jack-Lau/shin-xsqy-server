/*
 * Created 2018-9-19 17:59:54
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class EquipmentSchoolStrengthenLoader extends XmlMapContainerResourceLoader<EquipmentSchoolStrengthen> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSchoolStrengthen.xml";
    }

    @Override
    public Class<EquipmentSchoolStrengthen> getSupportedClass() {
        return EquipmentSchoolStrengthen.class;
    }

}
