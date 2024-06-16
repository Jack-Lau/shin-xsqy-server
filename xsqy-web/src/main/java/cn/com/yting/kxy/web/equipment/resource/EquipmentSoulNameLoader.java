/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class EquipmentSoulNameLoader extends XmlMapContainerResourceLoader<EquipmentSoulName> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSoulName.xml";
    }

    @Override
    public Class<EquipmentSoulName> getSupportedClass() {
        return EquipmentSoulName.class;
    }

}
