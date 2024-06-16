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
public class CommodityLoader extends XmlMapContainerResourceLoader<Commodity> {

    @Override
    public String getDefaultResourceName() {
        return "sShopCommodity.xml";
    }

    @Override
    public Class<Commodity> getSupportedClass() {
        return Commodity.class;
    }

}
