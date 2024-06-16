/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class LuckyInfoLoader extends XmlMapContainerResourceLoader<LuckyInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sLuckyInfo.xml";
    }

    @Override
    public Class<LuckyInfo> getSupportedClass() {
        return LuckyInfo.class;
    }

}
