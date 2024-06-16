/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.chat.model;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class BroadcastInfoLoader extends XmlMapContainerResourceLoader<BroadcastInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sBroadcastInfo.xml";
    }

    @Override
    public Class<BroadcastInfo> getSupportedClass() {
        return BroadcastInfo.class;
    }

}
