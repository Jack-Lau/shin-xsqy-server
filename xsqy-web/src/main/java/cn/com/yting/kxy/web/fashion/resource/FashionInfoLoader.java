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
public class FashionInfoLoader extends XmlMapContainerResourceLoader<FashionInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sFashionInfo.xml";
    }

    @Override
    public Class<FashionInfo> getSupportedClass() {
        return FashionInfo.class;
    }

}
