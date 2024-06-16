/*
 * Created 2018-12-13 17:05:38
 */
package cn.com.yting.kxy.web.game.mingjiandahui.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class KingBattleRankLoader extends XmlMapContainerResourceLoader<KingBattleRank> {

    @Override
    public String getDefaultResourceName() {
        return "sKingBattleRank.xml";
    }

    @Override
    public Class<KingBattleRank> getSupportedClass() {
        return KingBattleRank.class;
    }

    public int nextRank(int grade) {
        if (grade > getMap().size()) {
            return grade;
        }
        String prefix = get(grade).getCRank().substring(0, 3);
        grade++;
        while (grade <= getMap().size()) {
            if (!getMap().get(Long.valueOf(grade)).getCRank().startsWith(prefix)) {
                return grade;
            }
            grade++;
        }
        return grade;
    }

    public int previousRank(int grade) {
        if (grade <= 0) {
            return grade;
        }
        String prefix = get(grade).getCRank().substring(0, 3);
        grade--;
        while (grade > 0) {
            if (!getMap().get(Long.valueOf(grade)).getCRank().startsWith(prefix)) {
                return grade;
            }
            grade--;
        }
        return grade;
    }
}
