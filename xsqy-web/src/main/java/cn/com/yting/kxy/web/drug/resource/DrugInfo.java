/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class DrugInfo implements Resource {
    
    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private long effect_target;
    @XmlElement
    private String attr_name_1;
    @XmlElement
    private double attr_value_1;
    @XmlElement
    private String attr_name_2;
    @XmlElement
    private double attr_value_2;
    @XmlElement
    private long duration;
    @XmlElement
    private double value_percent_low;
    @XmlElement
    private double value_percent_high;
    
}
