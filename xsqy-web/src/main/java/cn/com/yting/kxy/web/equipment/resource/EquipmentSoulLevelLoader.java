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
public class EquipmentSoulLevelLoader extends XmlMapContainerResourceLoader<EquipmentSoulLevel> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentSoulLevel.xml";
    }

    @Override
    public Class<EquipmentSoulLevel> getSupportedClass() {
        return EquipmentSoulLevel.class;
    }

}
