/*
 * Created 2018-11-13 18:07:31
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.SimpleParameter;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GodEquipment implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private long prototypeId;
    @XmlElements(@XmlElement(name = "attr", type = Attr.class))
    private List<Attr> attrs = new ArrayList<>();
    @XmlElement
    private int upperLimitEnhancementLevel;
    @XmlElement
    private int nowEnhancementLevel;
    @XmlElement
    private Long fourEffect;
    @XmlElement
    private Long tenEffect;
    @XmlElement
    private int limitedQuantity;
    @XmlElement
    private Integer nowNumber;

    private List<Parameter> baseParameters;

    public List<Parameter> getBaseParameters() {
        if (baseParameters == null) {
            baseParameters = attrs.stream()
                .map(it -> new SimpleParameter(it.outputAttribute, it.value))
                .collect(Collectors.toList());
        }
        return baseParameters;
    }

    public List<Long> getEffectIds() {
        return Stream.of(fourEffect, tenEffect)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Getter
    public static class Attr {

        @XmlElement
        private String outputAttribute;
        @XmlElement
        private double value;
    }
}
