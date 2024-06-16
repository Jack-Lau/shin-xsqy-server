/*
 * Created 2018-7-12 16:48:51
 */
package cn.com.yting.kxy.web.currency.resource;

import java.io.InputStream;
import java.util.Map;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoaderUtils;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class CurrencyLoader extends XmlMapContainerResourceLoader<Currency> {

    @Override
    protected Map<Long, Currency> createMap(ResourceContext resourceContext, InputStream inputStream) {
        return ResourceLoaderUtils.extractDataMap(inputStream, Currency.class);
    }

    @Override
    public String getDefaultResourceName() {
        return "sCurrency.xml";
    }

    @Override
    public Class<Currency> getSupportedClass() {
        return Currency.class;
    }
}
