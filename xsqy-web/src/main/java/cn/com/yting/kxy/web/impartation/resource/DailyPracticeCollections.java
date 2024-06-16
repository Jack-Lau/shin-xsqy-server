/*
 * Created 2018-11-21 17:10:32
 */
package cn.com.yting.kxy.web.impartation.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class DailyPracticeCollections extends DailyPracticeAndAchievementCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int random;
    @XmlElements(@XmlElement(name = "dailyPractice", type = DailyPractice.class))
    private List<DailyPractice> dailyPractices = new ArrayList<>();

    private RandomSelector<DailyPracticeAndAchievement> selector;

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<DailyPracticeAndAchievement> builder = RandomSelector.builder();
        dailyPractices.forEach(it -> builder.add(() -> resourceContext.getLoader(DailyPracticeAndAchievementCollectionSupplier.class).get(it.id).get(), it.probability));
        selector = builder.build(random == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    @Override
    public Collection<DailyPracticeAndAchievement> get() {
        return selector.get();
    }

    @Getter
    public static class DailyPractice {

        @XmlElement
        private long id;
        @XmlElement
        private String name;
        @XmlElement
        private double probability;
    }
}
