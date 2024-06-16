/*
 * Created 2018-10-31 11:09:00
 */
package cn.com.yting.kxy.web.ranking.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GenericRankingInfoLoader extends XmlMapContainerResourceLoader<GenericRankingInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sGenericRankingInfo.xml";
    }

    @Override
    public Class<GenericRankingInfo> getSupportedClass() {
        return GenericRankingInfo.class;
    }

}
