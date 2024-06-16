/*
 * Created 2018-10-16 17:53:49
 */
package cn.com.yting.kxy.web.activity.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class ActivityOtherInfoLoader extends XmlMapContainerResourceLoader<ActivityOtherInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sActivityOtherInfo.xml";
    }

    @Override
    public Class<ActivityOtherInfo> getSupportedClass() {
        return ActivityOtherInfo.class;
    }
}
