/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Darkholme
 */
public class SecretShopJackpotLoader extends XmlMapContainerResourceLoader<SecretShopJackpot> {

    @Override
    public String getDefaultResourceName() {
        return "sSecretShopJackpot.xml";
    }

    @Override
    public Class<SecretShopJackpot> getSupportedClass() {
        return SecretShopJackpot.class;
    }

}
