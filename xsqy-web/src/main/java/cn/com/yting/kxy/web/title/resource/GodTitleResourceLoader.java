/*
 * Created 2018-11-16 21:09:55
 */
package cn.com.yting.kxy.web.title.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GodTitleResourceLoader extends XmlMapContainerResourceLoader<GodTitle> {

    @Override
    public String getDefaultResourceName() {
        return "sGodTitle.xml";
    }

    @Override
    public Class<GodTitle> getSupportedClass() {
        return GodTitle.class;
    }

}
