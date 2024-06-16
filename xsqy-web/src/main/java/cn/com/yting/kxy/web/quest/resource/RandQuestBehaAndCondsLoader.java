/*
 * Created 2018-8-3 10:54:31
 */
package cn.com.yting.kxy.web.quest.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class RandQuestBehaAndCondsLoader extends XmlMapContainerResourceLoader<RandQuestBehaAndConds> {

    @Override
    public String getDefaultResourceName() {
        return "sRandQuestBehaAndConds.xml";
    }

    @Override
    public Class<RandQuestBehaAndConds> getSupportedClass() {
        return RandQuestBehaAndConds.class;
    }

}
