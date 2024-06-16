/*
 * Created 2018-8-3 16:32:14
 */
package cn.com.yting.kxy.web.award.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.QuadraticFunction;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceReference;
import cn.com.yting.kxy.web.award.model.Award;
import cn.com.yting.kxy.web.award.model.AwardBaseElement;
import cn.com.yting.kxy.web.award.model.AwardBroadCastElement;
import cn.com.yting.kxy.web.award.model.AwardBuilder;
import cn.com.yting.kxy.web.award.model.AwardCurrencyElement;
import cn.com.yting.kxy.web.award.model.AwardElement;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class Awards implements Resource, AwardElement {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private PlayerExp playerExp;
    @XmlElement
    private Gold gold;
    @XmlElement
    private int randomType;
    @XmlElement
    private int kc;
    @XmlElements(
            @XmlElement(name = "awardChance", type = AwardChance.class)
    )
    private List<AwardChance> awardChances = new ArrayList<>();
    @XmlElement
    private Long equipmentPrototypeId;
    @XmlElement
    private Long petPrototypeId;
    @XmlElement
    private Long broadcastId;

    private List<AwardElement> awardElements = new ArrayList<>();
    private RandomSelector<AwardElement> selector;

    void afterUnmarshal(Unmarshaller u, Object parent) {
        if (playerExp == null) {
            playerExp = new PlayerExp();
        }
        if (gold == null) {
            gold = new Gold();
        }
        awardElements.add(new AwardBaseElement(
                new QuadraticFunction(playerExp.twoCoefficient, playerExp.oneCoefficient, playerExp.constant),
                new QuadraticFunction(gold.twoCoefficient, gold.oneCoefficient, gold.constant))
        );
        if (broadcastId != null) {
            awardElements.add(new AwardBroadCastElement(broadcastId));
        }
        if (kc != 0) {
            awardElements.add(new AwardCurrencyElement(CurrencyConstants.ID_毫仙石, kc * 1000));
        }
    }

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<AwardElement> builder = RandomSelector.builder();
        for (AwardChance awardChance : awardChances) {
            if (awardChance.awardCurrencyId >= 1000 && awardChance.awardCurrencyId <= 9999) {
                ResourceReference<Awards> ref = resourceContext.createReference(Awards.class, awardChance.awardCurrencyId);
                builder.add(() -> Collections.singleton(ref.get()), awardChance.probability);
            } else {
                builder.add(new AwardCurrencyElement(awardChance.awardCurrencyId, awardChance.amount), awardChance.probability);
            }
        }
        selector = builder.build(randomType == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    @Override
    public void apply(AwardBuilder builder, int playerLevel, long playerFc) {
        awardElements.forEach(it -> it.apply(builder, playerLevel, playerFc));
        Collection<AwardElement> selectedElements = selector.get();
        selectedElements.forEach(it -> it.apply(builder, playerLevel, playerFc));
    }

    public Award createAward(int playerLevel, long playerFc) {
        AwardBuilder builder = new AwardBuilder();
        apply(builder, playerLevel, playerFc);
        if (equipmentPrototypeId != null) {
            builder.addEquipmentPrototypeId(equipmentPrototypeId);
        }
        if (petPrototypeId != null) {
            builder.addPetPrototypeId(petPrototypeId);
        }
        return builder.build();
    }

    public static Awards getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(Awards.class).get(id);
    }

    @Getter
    public static class PlayerExp {

        @XmlElement
        private double twoCoefficient;
        @XmlElement
        private double oneCoefficient;
        @XmlElement
        private double constant;

    }

    @Getter
    public static class Gold {

        @XmlElement
        private double twoCoefficient;
        @XmlElement
        private double oneCoefficient;
        @XmlElement
        private double constant;

    }

    @Getter
    public static class AwardChance {

        @XmlElement
        private long awardCurrencyId;
        @XmlElement
        private String awardCurrencyName;
        @XmlElement
        private long amount;
        @XmlElement
        private double probability;

    }
}
