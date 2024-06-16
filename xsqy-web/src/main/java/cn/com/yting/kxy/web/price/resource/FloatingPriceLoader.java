/*
 * Created 2018-9-19 19:06:13
 */
package cn.com.yting.kxy.web.price.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class FloatingPriceLoader extends XmlMapContainerResourceLoader<FloatingPrice> {

    @Override
    public String getDefaultResourceName() {
        return "sFloatingPrice.xml";
    }

    @Override
    public Class<FloatingPrice> getSupportedClass() {
        return FloatingPrice.class;
    }
}
