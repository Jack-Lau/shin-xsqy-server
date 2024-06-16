/*
 * Created 2018-11-22 16:38:49
 */
package cn.com.yting.kxy.web.impartation.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class ContributionAndExpGetProportionLoader extends XmlMapContainerResourceLoader<ContributionAndExpGetProportion> {

    @Override
    public String getDefaultResourceName() {
        return "sContributionAndExpGetProportion.xml";
    }

    @Override
    public Class<ContributionAndExpGetProportion> getSupportedClass() {
        return ContributionAndExpGetProportion.class;
    }
}
