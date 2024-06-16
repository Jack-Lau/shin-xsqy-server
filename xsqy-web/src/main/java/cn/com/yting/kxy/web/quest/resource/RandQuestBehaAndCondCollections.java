/*
 * Created 2018-8-3 10:47:13
 */
package cn.com.yting.kxy.web.quest.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceReference;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class RandQuestBehaAndCondCollections implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int randomType;
    @XmlElements(
        @XmlElement(name = "randBehaAndCond", type = RandBehaAndCond.class)
    )
    private List<RandBehaAndCond> randBehaAndConds = new ArrayList<>();

    @Getter
    public static class RandBehaAndCond {

        @XmlElement
        private long id;
        @XmlElement
        private String content;
        @XmlElement
        private int probability;

    }

    private RandomSelector<RandQuestBehaAndConds> selector;

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<RandQuestBehaAndConds> builder = RandomSelector.builder();
        for (RandBehaAndCond randBehaAndCond : randBehaAndConds) {
            if (randBehaAndCond.id >= 870000 && randBehaAndCond.id <= 879999) {
                ResourceReference<RandQuestBehaAndConds> ref = resourceContext.createReference(RandQuestBehaAndConds.class, randBehaAndCond.id);
                builder.add(() -> Collections.singleton(ref.get()), randBehaAndCond.probability);
            } else if (randBehaAndCond.id >= 880000 && randBehaAndCond.id <= 889999) {
                ResourceReference<RandQuestBehaAndCondCollections> ref = resourceContext.createReference(RandQuestBehaAndCondCollections.class, randBehaAndCond.id);
                builder.add(() -> ref.get().getSelector().get(), randBehaAndCond.probability);
            } else {
                throw new IllegalStateException("无效的id：" + randBehaAndCond.id);
            }
        }
        selector = builder.build(randomType == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    public static RandQuestBehaAndCondCollections getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(RandQuestBehaAndCondCollections.class).get(id);
    }
}
