/*
 * Created 2019-1-8 11:21:26
 */
package cn.com.yting.kxy.web.perk.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpaceBuilder;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SingleElementParameterSpace;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.web.perk.Perk;
import cn.com.yting.kxy.web.perk.PerkSelection;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class Talent implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long schoolId;
    @XmlElement
    private int position;
    @XmlElement
    private String incrParam;
    @XmlElement
    private double oneCoefficient;
    @XmlElement
    private double constant;
    @XmlElements(@XmlElement(name = "talent", type = TalentElement.class))
    private List<TalentElement> talents;

    public Perk createPerk(int rank, PerkSelection selection) {
        List<ParameterSpace> parameterSpaces = new ArrayList<>();
        parameterSpaces.add(new ParameterSpaceBuilder()
            .simple(incrParam, oneCoefficient * rank + constant)
            .simple(ParameterNameConstants.战斗力, rank * 19)
            .build());
        if (selection.equals(PerkSelection.YANG)) {
            parameterSpaces.add(talents.get(0).createParameterSpace(rank));
        } else if (selection.equals(PerkSelection.YIN)) {
            parameterSpaces.add(talents.get(1).createParameterSpace(rank));
        }
        return new Perk(rank, selection, new AggregateParameterSpace(parameterSpaces));
    }

    @Getter
    public static class TalentElement {

        @XmlElement
        private String name;
        @XmlElement
        private double oneCoefficient;
        @XmlElement
        private double constant;
        @XmlElement
        private String description;
        @XmlElement
        private String nextLevel;

        public ParameterSpace createParameterSpace(int rank) {
            return new SingleElementParameterSpace(name, new SimpleParameterBase(oneCoefficient * (rank / 10) + constant));
        }
    }
}
