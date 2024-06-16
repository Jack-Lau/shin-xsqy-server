/*
 * Created 2019-1-23 15:36:09
 */
package cn.com.yting.kxy.web.game.zaixianjiangli.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class GameOnlineTimeLoader extends XmlMapContainerResourceLoader<GameOnlineTime> {

    @Override
    public String getDefaultResourceName() {
        return "sGameOnlineTime.xml";
    }

    @Override
    public Class<GameOnlineTime> getSupportedClass() {
        return GameOnlineTime.class;
    }

}
