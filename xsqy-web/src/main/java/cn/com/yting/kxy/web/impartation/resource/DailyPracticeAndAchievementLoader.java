/*
 * Created 2018-11-21 17:08:28
 */
package cn.com.yting.kxy.web.impartation.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class DailyPracticeAndAchievementLoader extends XmlMapContainerResourceLoader<DailyPracticeAndAchievement> {

    @Override
    public String getDefaultResourceName() {
        return "sDailyPracticeAndAchievement.xml";
    }

    @Override
    public Class<DailyPracticeAndAchievement> getSupportedClass() {
        return DailyPracticeAndAchievement.class;
    }
}
