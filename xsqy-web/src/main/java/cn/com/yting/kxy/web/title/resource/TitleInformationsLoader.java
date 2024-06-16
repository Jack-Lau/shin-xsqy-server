/*
 * Created 2018-11-7 18:32:45
 */
package cn.com.yting.kxy.web.title.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class TitleInformationsLoader extends XmlMapContainerResourceLoader<TitleInformations> {

    @Override
    public String getDefaultResourceName() {
        return "sTitleInformations.xml";
    }

    @Override
    public Class<TitleInformations> getSupportedClass() {
        return TitleInformations.class;
    }
}
