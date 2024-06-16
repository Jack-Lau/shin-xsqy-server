/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.buff.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class BuffParamLoader extends XmlMapContainerResourceLoader<BuffParam> {

    @Override
    public String getDefaultResourceName() {
        return "sBuffInfo.xml";
    }

    @Override
    public Class<BuffParam> getSupportedClass() {
        return BuffParam.class;
    }

}
