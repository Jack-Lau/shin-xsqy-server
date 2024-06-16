/*
 * Created 2018-9-13 16:52:35
 */
package cn.com.yting.kxy.web.school.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.QuadraticFunction;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class SchoolAbilityInformation implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long name;
    @XmlElement
    private long maxLevel;
    @XmlElements(
        @XmlElement(name = "incrParam", type = IncrParam.class)
    )
    private List<IncrParam> incrParams = new ArrayList<>();
    @XmlElements(
        @XmlElement(name = "skill", type = Skill.class)
    )
    private List<Skill> skills = new ArrayList<>();
    @XmlElements(
        @XmlElement(name = "effect", type = Effect.class)
    )
    private List<Effect> effects = new ArrayList<>();

    @Getter
    public static class IncrParam {

        @XmlElement
        private String name;
        @XmlElement
        private double oneCoefficient;
        @XmlElement
        private double constant;

        public ParameterBase applyToLevel(int level) {
            return new SimpleParameterBase(new QuadraticFunction(0, oneCoefficient, constant).applyAsDouble(level));
        }
    }

    @Getter
    public static class Skill {

        @XmlElement
        private long id;
        @XmlElement
        private String name;
        @XmlElement
        private int abilityLvRequirement;
    }

    @Getter
    public static class Effect {

        @XmlElement
        private long id;
        @XmlElement
        private String name;
        @XmlElement
        private int abilityLvRequirement;
    }
}
