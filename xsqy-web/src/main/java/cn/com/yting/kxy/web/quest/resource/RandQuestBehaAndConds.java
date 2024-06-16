/*
 * Created 2018-8-2 18:36:12
 */
package cn.com.yting.kxy.web.quest.resource;

import java.util.List;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.quest.model.objective.Objective;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class RandQuestBehaAndConds implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String fixBehaviors;
    @XmlElement
    private String fixConditions;

    private List<Objective> fixedObjectives;

    void afterUnmarshal(Unmarshaller u, Object parent) {
        fixedObjectives = Objective.fromConfigText(fixConditions);
    }

    public static RandQuestBehaAndConds getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(RandQuestBehaAndConds.class).get(id);
    }
}
