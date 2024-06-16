/*
 * Created 2019-1-8 11:26:49
 */
package cn.com.yting.kxy.web.perk.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class TalentLoader extends XmlMapContainerResourceLoader<Talent> {

    @Override
    public String getDefaultResourceName() {
        return "sTalent.xml";
    }

    @Override
    public Class<Talent> getSupportedClass() {
        return Talent.class;
    }
}
