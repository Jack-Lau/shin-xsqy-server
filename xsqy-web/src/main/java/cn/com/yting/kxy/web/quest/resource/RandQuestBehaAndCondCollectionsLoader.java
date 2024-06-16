/*
 * Created 2018-8-3 10:59:38
 */
package cn.com.yting.kxy.web.quest.resource;

import java.io.InputStream;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class RandQuestBehaAndCondCollectionsLoader extends XmlMapContainerResourceLoader<RandQuestBehaAndCondCollections> {

    @Override
    public void reload(ResourceContext context, InputStream input) {
        super.reload(context, input);
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sRandQuestBehaAndCondCollections.xml";
    }

    @Override
    public Class<RandQuestBehaAndCondCollections> getSupportedClass() {
        return RandQuestBehaAndCondCollections.class;
    }

}
