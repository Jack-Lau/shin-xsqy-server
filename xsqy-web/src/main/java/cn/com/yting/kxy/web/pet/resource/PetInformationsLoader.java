/*
 * Created 2018-10-10 17:06:20
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PetInformationsLoader extends XmlMapContainerResourceLoader<PetInformations> {

    @Override
    public String getDefaultResourceName() {
        return "sPetInformations.xml";
    }

    @Override
    public Class<PetInformations> getSupportedClass() {
        return PetInformations.class;
    }

}
