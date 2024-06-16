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
public class GoldTowerQuestionLoader extends XmlMapContainerResourceLoader<GoldTowerQuestion> {

    @Override
    public String getDefaultResourceName() {
        return "sGoldTowerQuestions.xml";
    }

    @Override
    public Class<GoldTowerQuestion> getSupportedClass() {
        return GoldTowerQuestion.class;
    }

}
