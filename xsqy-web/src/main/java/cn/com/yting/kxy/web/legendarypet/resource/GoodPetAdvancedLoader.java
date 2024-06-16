/*
 * Created 2019-1-23 18:09:23
 */
package cn.com.yting.kxy.web.legendarypet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GoodPetAdvancedLoader extends XmlMapContainerResourceLoader<GoodPetAdvanced> {

    @Override
    public String getDefaultResourceName() {
        return "sGoodPetAdvanced.xml";
    }

    @Override
    public Class<GoodPetAdvanced> getSupportedClass() {
        return GoodPetAdvanced.class;
    }

}
