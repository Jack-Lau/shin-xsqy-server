/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class FishCategoryLoader extends XmlMapContainerResourceLoader<FishCategory> {

    @Override
    public String getDefaultResourceName() {
        return "sFishCategory.xml";
    }

    @Override
    public Class<FishCategory> getSupportedClass() {
        return FishCategory.class;
    }

}
