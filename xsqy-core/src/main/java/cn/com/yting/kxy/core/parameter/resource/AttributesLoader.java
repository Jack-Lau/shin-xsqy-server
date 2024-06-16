/*
 * Created 2018-8-8 17:20:39
 */
package cn.com.yting.kxy.core.parameter.resource;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpaceBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class AttributesLoader extends XmlMapContainerResourceLoader<Attributes> {

    private ParameterSpace petBaseParameterSpace;

    @Override
    protected void afterReload(ResourceContext context) {
        ParameterSpaceBuilder builder = ParameterSpace.builder();
        getMap().values().stream()
            .filter(it -> it.getBasicValueOfPet()!= 0)
            .forEach(it -> builder.simple(it.getName(), it.getBasicValueOfPet()));
        petBaseParameterSpace = builder.build();
    }

    public ParameterSpace getPetBaseParameterSpace() {
        return petBaseParameterSpace;
    }

    @Override
    public String getDefaultResourceName() {
        return "sAttributes.xml";
    }

    @Override
    public Class<Attributes> getSupportedClass() {
        return Attributes.class;
    }

}
