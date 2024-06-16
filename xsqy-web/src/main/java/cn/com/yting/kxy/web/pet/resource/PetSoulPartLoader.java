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
public class PetSoulPartLoader extends XmlMapContainerResourceLoader<PetSoulPart> {

    @Override
    public String getDefaultResourceName() {
        return "sPetSoulPart.xml";
    }

    @Override
    public Class<PetSoulPart> getSupportedClass() {
        return PetSoulPart.class;
    }

}
