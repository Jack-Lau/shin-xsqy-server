/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class EquipmentCollectionLoader extends XmlMapContainerResourceLoader<EquipmentCollection> {

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentCollection.xml";
    }

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.resourceContext = context);
    }

    @Override
    public Class<EquipmentCollection> getSupportedClass() {
        return EquipmentCollection.class;
    }

}
