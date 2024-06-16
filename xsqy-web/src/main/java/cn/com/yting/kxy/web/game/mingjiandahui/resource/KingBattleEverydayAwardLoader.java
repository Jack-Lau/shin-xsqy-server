/*
 * Created 2018-12-14 16:33:43
 */
package cn.com.yting.kxy.web.game.mingjiandahui.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class KingBattleEverydayAwardLoader extends XmlMapContainerResourceLoader<KingBattleEverydayAward> {

    @Override
    public String getDefaultResourceName() {
        return "sKingBattleEverydayAward.xml";
    }

    @Override
    public Class<KingBattleEverydayAward> getSupportedClass() {
        return KingBattleEverydayAward.class;
    }

}
