/*
 * Created 2018-8-3 16:37:31
 */
package cn.com.yting.kxy.web.award.resource;

import java.io.InputStream;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class AwardsLoader extends XmlMapContainerResourceLoader<Awards> {

    @Override
    public void reload(ResourceContext context, InputStream input) {
        super.reload(context, input);
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sAwards.xml";
    }

    @Override
    public Class<Awards> getSupportedClass() {
        return Awards.class;
    }

}
