/*
 * Created 2018-10-31 11:17:58
 */
package cn.com.yting.kxy.web.ranking.resource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import cn.com.yting.kxy.web.ranking.resource.GenericRankingAward.Model;
import lombok.AllArgsConstructor;

/**
 *
 * @author Azige
 */
public class GenericRankingAwardLoader extends XmlMapContainerResourceLoader<GenericRankingAward> {

    private Map<Long, List<Model>> awardModelMap;

    @Override
    protected void afterReload(ResourceContext context) {
        @AllArgsConstructor
        class RankingAndModel {

            long ranking;
            Model model;
        }
        awardModelMap = getMap().values().stream()
            .flatMap(data -> data.getModels().stream()
                .map(model -> new RankingAndModel(data.getId(), model)))
            .sorted(Comparator.comparing(ram -> ram.ranking))
            .collect(Collectors.groupingBy(ram -> ram.model.getId(), Collectors.mapping(ram -> ram.model, Collectors.toList())));
    }

    @Override
    public String getDefaultResourceName() {
        return "sGenericRankingAward.xml";
    }

    @Override
    public Class<GenericRankingAward> getSupportedClass() {
        return GenericRankingAward.class;
    }

    public List<Model> getByAwardModelId(long awardModelId) {
        return awardModelMap.get(awardModelId);
    }
}
