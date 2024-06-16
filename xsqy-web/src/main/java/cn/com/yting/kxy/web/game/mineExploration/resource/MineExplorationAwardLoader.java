/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class MineExplorationAwardLoader extends XmlMapContainerResourceLoader<MineExplorationAward> {

    @Override
    public String getDefaultResourceName() {
        return "sMineExplorationAward.xml";
    }

    @Override
    public Class<MineExplorationAward> getSupportedClass() {
        return MineExplorationAward.class;
    }

}
