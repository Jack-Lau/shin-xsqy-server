/*
 * Created 2018-10-12 11:07:54
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PetSkillCollectionsLoader extends XmlMapContainerResourceLoader<PetSkillCollections> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.buildSelector(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sPetSkillCollections.xml";
    }

    @Override
    public Class<PetSkillCollections> getSupportedClass() {
        return PetSkillCollections.class;
    }

}
