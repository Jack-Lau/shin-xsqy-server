/*
 * Created 2018-10-24 11:22:37
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PetAbilityStudyLoader extends XmlMapContainerResourceLoader<PetAbilityStudy> {

    @Override
    public String getDefaultResourceName() {
        return "sPetAbilityStudy.xml";
    }

    @Override
    public Class<PetAbilityStudy> getSupportedClass() {
        return PetAbilityStudy.class;
    }
}
