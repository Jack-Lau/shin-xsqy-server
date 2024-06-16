/*
 * Created 2018-10-11 19:30:34
 */
package cn.com.yting.kxy.web.pet.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SingleElementParameterSpace;
import lombok.Getter;

/**
 *
 * @author Azige
 */
// c宠物技能表.xlsx revision 12274
@Getter
public class PetAbilityInformation extends PetAbilityInformationCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private int mainPet;
    @XmlElement
    private int materialPet;
    @XmlElement
    private int classification;
    @XmlElement
    private int abilityClass;
    @XmlElement
    private int icon;
    @XmlElements(@XmlElement(name = "incrParam", type = IncrParam.class))
    private List<IncrParam> incrParams = new ArrayList<>();
    @XmlElement
    private Integer skillId;
    @XmlElement
    private String skillName;
    @XmlElement
    private String description;

    public ParameterSpace createParameterSpace(int referenceLevel) {
        List<ParameterSpace> parameterSpaces = incrParams.stream()
            .map(it -> it.createParameterSpace(referenceLevel))
            .collect(Collectors.toCollection(ArrayList::new));
        int fc = abilityClass == 0 ? 250 : 650;
        parameterSpaces.add(new SingleElementParameterSpace(ParameterNameConstants.战斗力, new SimpleParameterBase(fc)));
        return new AggregateParameterSpace(parameterSpaces);
    }

    @Override
    public Collection<PetAbilityInformation> get() {
        return Collections.singleton(this);
    }

    @Getter
    public static class IncrParam {

        @XmlElement
        private String name;
        @XmlElement
        private int incrType;
        @XmlElement
        private double parameterOne;
        @XmlElement
        private double parameterTwo;

        public ParameterSpace createParameterSpace(int referenceLevel) {
            switch (incrType) {
                case 1:
                    return new SingleElementParameterSpace(name, new SimpleParameterBase(parameterOne));
                case 2:
                    return new SingleElementParameterSpace(name, new SimpleParameterBase(parameterOne * referenceLevel + parameterTwo));
                default:
                    return ParameterSpace.EMPTY;
            }
        }
    }
}
