/*
 * Created 2018-8-1 15:20:59
 */
package cn.com.yting.kxy.web.quest.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resetting.Resetable;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.quest.model.AwardConfig;
import cn.com.yting.kxy.web.quest.model.StatusConfig;
import cn.com.yting.kxy.web.quest.model.objective.Objective;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Azige
 */
@Getter
@ToString
public class SequenceQuest implements Resource, Resetable {

    @XmlAttribute
    private long id;
    @XmlElement
    private String preQuestResults = "";
    @XmlElement
    private int pickupLvRequire;
    @XmlElement
    private String activityRequire;
    @XmlElement
    private Integer maxPickupCount;
    @XmlElement
    private Integer pickupCountReset;
    @XmlElement
    private String fixBehaviors;
    @XmlElement
    private String fixConditions;
    @XmlElement
    private long randBehaAndCond;
    @XmlElement
    private String stateTransition;
    @XmlElement
    private String award;

    private List<Objective> fixedObjectives;
    private List<StatusConfig> statusConfigs;
    private List<AwardConfig> awardConfigs;
    private Set<Long> preRequireDependencyIds;
    private Set<Long> activateIds;
    private ResetType resetType;

    public void setActivateIds(Set<Long> activateIds) {
        this.activateIds = activateIds;
    }

    public boolean isFixedObjective() {
        return fixConditions != null;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (fixConditions != null) {
            fixedObjectives = Objective.fromConfigText(fixConditions);
        }
        if (stateTransition == null) {
            throw new IllegalStateException("stateTransition 为空，id=" + id);
        }
        statusConfigs = StatusConfig.fromText(stateTransition);
        if (award == null) {
            throw new IllegalStateException("award 为空，id=" + id);
        }
        awardConfigs = AwardConfig.fromText(award);
        preRequireDependencyIds = createPreRequireDepencencyIds(preQuestResults);
        resetType = configToResetType(pickupCountReset);
    }

    private static Set<Long> createPreRequireDepencencyIds(String preQuestResults) {
        Matcher matcher = Pattern.compile("\\d+").matcher(preQuestResults);
        Set<Long> result = new HashSet<>();
        while (matcher.find()) {
            result.add(Long.parseLong(matcher.group()));
        }
        return result;
    }

    private static ResetType configToResetType(Integer pickupCountReset) {
        if (pickupCountReset == null) {
            return ResetType.NEVER;
        } else {
            switch (pickupCountReset) {
                case 0:
                    return ResetType.NEVER;
                case 1:
                    return ResetType.DAILY;
                case 2:
                    return ResetType.WEEKLY;
                default:
                    throw new IllegalStateException("无效的 pickupCountReset 值：" + pickupCountReset);
            }
        }
    }

    public static SequenceQuest getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(SequenceQuest.class).get(id);
    }
}
