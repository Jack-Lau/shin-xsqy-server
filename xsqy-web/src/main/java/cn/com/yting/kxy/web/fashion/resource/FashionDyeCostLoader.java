/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class FashionDyeCostLoader extends XmlMapContainerResourceLoader<FashionDyeCost> {

    @Override
    public String getDefaultResourceName() {
        return "sFashionDyeCost.xml";
    }

    @Override
    public Class<FashionDyeCost> getSupportedClass() {
        return FashionDyeCost.class;
    }

}
