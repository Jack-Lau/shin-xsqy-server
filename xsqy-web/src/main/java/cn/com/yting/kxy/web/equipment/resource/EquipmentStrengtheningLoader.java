/*
 * Created 2018-9-18 15:40:06
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class EquipmentStrengtheningLoader extends XmlMapContainerResourceLoader<EquipmentStrengthening> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentStrengthening.xml";
    }

    @Override
    public Class<EquipmentStrengthening> getSupportedClass() {
        return EquipmentStrengthening.class;
    }

}
