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
public class MineExplorationAddLifeLoader extends XmlMapContainerResourceLoader<MineExplorationAddLife> {

    @Override
    public String getDefaultResourceName() {
        return "sMineExplorationAddLife.xml";
    }

    @Override
    public Class<MineExplorationAddLife> getSupportedClass() {
        return MineExplorationAddLife.class;
    }

}
