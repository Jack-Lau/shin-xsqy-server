/*
 * Created 2018-11-13 17:36:19
 */
package cn.com.yting.kxy.web.auction.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class BlackMarketAuctionInfoLoader extends XmlMapContainerResourceLoader<BlackMarketAuctionInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sBlackMarketAuctionInfo.xml";
    }

    @Override
    public Class<BlackMarketAuctionInfo> getSupportedClass() {
        return BlackMarketAuctionInfo.class;
    }

}
