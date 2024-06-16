/*
 * Created 2018-11-13 18:34:54
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GodPetLoader extends XmlMapContainerResourceLoader<GodPet> {

    @Override
    public String getDefaultResourceName() {
        return "sGodPet.xml";
    }

    @Override
    public Class<GodPet> getSupportedClass() {
        return GodPet.class;
    }
}
