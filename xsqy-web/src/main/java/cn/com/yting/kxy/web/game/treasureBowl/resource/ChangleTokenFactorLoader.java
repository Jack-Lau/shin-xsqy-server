/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class ChangleTokenFactorLoader extends XmlMapContainerResourceLoader<ChangleTokenFactor> {

    @Override
    public String getDefaultResourceName() {
        return "sChangleTokenFactor.xml";
    }

    @Override
    public Class<ChangleTokenFactor> getSupportedClass() {
        return ChangleTokenFactor.class;
    }

}
