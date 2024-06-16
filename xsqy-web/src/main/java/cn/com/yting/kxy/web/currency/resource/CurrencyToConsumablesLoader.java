/*
 * Created 2018-11-9 17:10:25
 */
package cn.com.yting.kxy.web.currency.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class CurrencyToConsumablesLoader extends XmlMapContainerResourceLoader<CurrencyToConsumables> {

    @Override
    public String getDefaultResourceName() {
        return "sCurrencyToConsumables.xml";
    }

    @Override
    public Class<CurrencyToConsumables> getSupportedClass() {
        return CurrencyToConsumables.class;
    }
}
