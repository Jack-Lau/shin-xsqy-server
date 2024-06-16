/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class GoldTowerQuestionCollectionLoader extends XmlMapContainerResourceLoader<GoldTowerQuestionCollection> {

    @Override
    public String getDefaultResourceName() {
        return "sGoldTowerQuestionCollections.xml";
    }

    @Override
    public Class<GoldTowerQuestionCollection> getSupportedClass() {
        return GoldTowerQuestionCollection.class;
    }

}
