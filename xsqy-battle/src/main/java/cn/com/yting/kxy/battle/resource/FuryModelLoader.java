/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class FuryModelLoader extends XmlMapContainerResourceLoader<FuryModel> {

    @Override
    public String getDefaultResourceName() {
        return "sFuryModel.xml";
    }

    @Override
    public Class<FuryModel> getSupportedClass() {
        return FuryModel.class;
    }

}
