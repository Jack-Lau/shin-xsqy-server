/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class ShopLoader extends XmlMapContainerResourceLoader<Shop> {

    @Override
    public String getDefaultResourceName() {
        return "sShop.xml";
    }

    @Override
    public Class<Shop> getSupportedClass() {
        return Shop.class;
    }

}
