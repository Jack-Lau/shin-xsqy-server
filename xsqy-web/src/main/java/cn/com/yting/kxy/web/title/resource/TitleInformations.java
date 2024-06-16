/*
 * Created 2018-11-7 18:21:40
 */
package cn.com.yting.kxy.web.title.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpaceBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class TitleInformations implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private int color;
    @XmlElements(@XmlElement(name = "attribute", type = Attribute.class))
    private List<Attribute> attributes = new ArrayList<>();
    @XmlElement
    private int fc;
    @XmlElement
    private String description;
    @XmlElement
    private int limitedQuantity;

    private ParameterSpace parameterSpace;

    public ParameterSpace getParameterSpace() {
        if (parameterSpace == null) {
            ParameterSpaceBuilder builder = new ParameterSpaceBuilder();
            attributes.forEach(it -> builder.simple(it.name, it.value));
            builder.simple(ParameterNameConstants.战斗力, fc);
            parameterSpace = builder.build();
        }
        return parameterSpace;
    }

    @Getter
    public static class Attribute {

        @XmlElement
        private String name;
        @XmlElement
        private double value;
    }
}
