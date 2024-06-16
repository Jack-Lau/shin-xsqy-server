/*
 * Created 2018-8-10 18:28:38
 */
package cn.com.yting.kxy.web.player.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PlayerLevelupExpLoader extends XmlMapContainerResourceLoader<PlayerLevelupExp> {

    @Override
    public String getDefaultResourceName() {
        return "sPlayerLevelupExp.xml";
    }

    @Override
    public Class<PlayerLevelupExp> getSupportedClass() {
        return PlayerLevelupExp.class;
    }
}
