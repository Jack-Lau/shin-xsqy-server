/*
 * Created 2018-7-26 15:43:57
 */
package cn.com.yting.kxy.web.gift.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GiftAndExchangeCodeInformationLoader extends XmlMapContainerResourceLoader<GiftAndExchangeCodeInformation> {

    @Override
    public String getDefaultResourceName() {
        return "sGiftAndExchangeCodeInformation.xml";
    }

    @Override
    public Class<GiftAndExchangeCodeInformation> getSupportedClass() {
        return GiftAndExchangeCodeInformation.class;
    }

}
