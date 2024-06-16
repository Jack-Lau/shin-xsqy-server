/*
 * Created 2018-9-19 17:53:06
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

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
public class EquipmentSchoolStrengthen extends EquipmentEffect {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private String parameter;
    @XmlElement
    private int fc;

    private ParameterSpace parameterSpace;

    @Override
    public ParameterSpace getParameterSpace() {
        if (parameterSpace == null) {
            Map<String, ParameterBase> map = new HashMap<>();
            map.put(parameter, new SimpleParameterBase(1));
            map.put(ParameterNameConstants.战斗力, new SimpleParameterBase(fc));
            parameterSpace = new SimpleParameterSpace(map);
        }
        return parameterSpace;
    }
}
