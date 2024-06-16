/*
 * Created 2018-9-12 16:05:46
 */
package cn.com.yting.kxy.web.school.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class SchoolInformationLoader extends XmlMapContainerResourceLoader<SchoolInformation> {

    @Override
    public String getDefaultResourceName() {
        return "sSchoolInformation.xml";
    }

    @Override
    public Class<SchoolInformation> getSupportedClass() {
        return SchoolInformation.class;
    }

}
