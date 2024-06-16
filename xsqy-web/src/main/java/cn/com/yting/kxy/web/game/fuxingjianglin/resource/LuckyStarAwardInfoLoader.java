/*
 * Created 2019-1-23 11:31:12
 */
package cn.com.yting.kxy.web.game.fuxingjianglin.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class LuckyStarAwardInfoLoader extends XmlMapContainerResourceLoader<LuckyStarAwardInfo> {

    @Override
    public String getDefaultResourceName() {
        return "sLuckyStarAwardInfo.xml";
    }

    @Override
    public Class<LuckyStarAwardInfo> getSupportedClass() {
        return LuckyStarAwardInfo.class;
    }

}
