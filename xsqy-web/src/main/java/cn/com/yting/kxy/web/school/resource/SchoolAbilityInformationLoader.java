/*
 * Created 2018-9-14 15:34:11
 */
package cn.com.yting.kxy.web.school.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class SchoolAbilityInformationLoader extends XmlMapContainerResourceLoader<SchoolAbilityInformation> {

    @Override
    public String getDefaultResourceName() {
        return "sSchoolAbilityInformation.xml";
    }

    @Override
    public Class<SchoolAbilityInformation> getSupportedClass() {
        return SchoolAbilityInformation.class;
    }

}
