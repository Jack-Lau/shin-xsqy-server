/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class DrugInfoLoader extends XmlMapContainerResourceLoader<DrugInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sDrugInfo.xml";
    }

    @Override
    public Class<DrugInfo> getSupportedClass() {
        return DrugInfo.class;
    }

}
