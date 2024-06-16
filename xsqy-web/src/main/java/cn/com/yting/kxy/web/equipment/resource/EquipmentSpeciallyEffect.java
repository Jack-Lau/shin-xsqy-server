/*
 * Created 2018-9-19 17:01:57
 */
package cn.com.yting.kxy.web.equipment.resource;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class EquipmentSpeciallyEffect extends EquipmentEffect {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElements(
        @XmlElement(name = "parameter", type = ParameterElement.class)
    )
    private List<ParameterElement> parameters;
    @XmlElement
    private int fc;
    @XmlElement
    private double blueRecast;
    @XmlElement
    private double purpleRecast;

    private ParameterSpace parameterSpace;

    @Override
    public ParameterSpace getParameterSpace() {
        if (parameterSpace == null) {
            Map<String, ParameterBase> map = new HashMap<>();
            parameters.forEach(it -> map.put(it.name, new SimpleParameterBase(it.value)));
            map.put(ParameterNameConstants.战斗力, new SimpleParameterBase(fc));
            parameterSpace = new SimpleParameterSpace(map);
        }
        return parameterSpace;
    }

    @Getter
    public static class ParameterElement {

        @XmlElement
        private String name;
        @XmlElement
        private double value;
    }
}
