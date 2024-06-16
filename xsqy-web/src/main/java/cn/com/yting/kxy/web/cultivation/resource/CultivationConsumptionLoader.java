/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.cultivation.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class CultivationConsumptionLoader extends XmlMapContainerResourceLoader<CultivationConsumption> {

    @Override
    public String getDefaultResourceName() {
        return "sSchoolCultivationConsumption.xml";
    }

    @Override
    public Class<CultivationConsumption> getSupportedClass() {
        return CultivationConsumption.class;
    }

}
