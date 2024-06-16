/*
 * Created 2018-9-12 12:16:23
 */
package cn.com.yting.kxy.web.school.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class SchoolAbilityConsumptionLoader extends XmlMapContainerResourceLoader<SchoolAbilityConsumption> {

    @Override
    public String getDefaultResourceName() {
        return "sSchoolAbilityConsumption.xml";
    }

    @Override
    public Class<SchoolAbilityConsumption> getSupportedClass() {
        return SchoolAbilityConsumption.class;
    }

}
