/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class PetSoulLevelLoader extends XmlMapContainerResourceLoader<PetSoulLevel> {

    @Override
    public String getDefaultResourceName() {
        return "sPetSoulLevel.xml";
    }

    @Override
    public Class<PetSoulLevel> getSupportedClass() {
        return PetSoulLevel.class;
    }

}
