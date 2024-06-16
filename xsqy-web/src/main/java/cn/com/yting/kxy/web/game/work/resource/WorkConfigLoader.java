/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.work.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Administrator
 */
public class WorkConfigLoader extends XmlMapContainerResourceLoader<WorkConfig> {

    @Override
    public String getDefaultResourceName() {
        return "sWorkConfig.xml";
    }

    @Override
    public Class<WorkConfig> getSupportedClass() {
        return WorkConfig.class;
    }

}
