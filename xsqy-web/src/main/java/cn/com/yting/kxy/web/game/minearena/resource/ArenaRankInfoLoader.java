/*
 * Created 2018-10-18 11:37:04
 */
package cn.com.yting.kxy.web.game.minearena.resource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class ArenaRankInfoLoader extends XmlMapContainerResourceLoader<ArenaRankInfo> {

    private Map<Integer, ArenaRankInfo> rankingMap;

    public ArenaRankInfo getByRanking(int ranking) {
        return rankingMap.get(ranking);
    }

    @Override
    protected void afterReload(ResourceContext context) {
        rankingMap = getMap().values().stream()
            .collect(Collectors.toMap(ArenaRankInfo::getRank, Function.identity(), (a, b) -> {
                throw new IllegalStateException("重复的排名：" + a.getRank());
            }));
    }

    @Override
    public String getDefaultResourceName() {
        return "sArenaRankInfo.xml";
    }

    @Override
    public Class<ArenaRankInfo> getSupportedClass() {
        return ArenaRankInfo.class;
    }

}
