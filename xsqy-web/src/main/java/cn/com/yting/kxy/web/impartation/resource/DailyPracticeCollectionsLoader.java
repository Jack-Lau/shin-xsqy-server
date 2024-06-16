/*
 * Created 2018-11-21 17:14:32
 */
package cn.com.yting.kxy.web.impartation.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class DailyPracticeCollectionsLoader extends XmlMapContainerResourceLoader<DailyPracticeCollections> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sDailyPracticeCollections.xml";
    }

    @Override
    public Class<DailyPracticeCollections> getSupportedClass() {
        return DailyPracticeCollections.class;
    }
}
