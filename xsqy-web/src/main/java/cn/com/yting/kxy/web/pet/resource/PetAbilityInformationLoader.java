/*
 * Created 2018-10-11 19:43:38
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PetAbilityInformationLoader extends XmlMapContainerResourceLoader<PetAbilityInformation> {

    @Override
    public String getDefaultResourceName() {
        return "sPetAbilityInformation.xml";
    }

    @Override
    public Class<PetAbilityInformation> getSupportedClass() {
        return PetAbilityInformation.class;
    }

}
