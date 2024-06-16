/*
 * Created 2018-9-19 19:04:20
 */
package cn.com.yting.kxy.web.price.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class FloatingPrice implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private long currency;
    @XmlElement
    private long start;
    @XmlElement
    private int cromoteCondition;
    @XmlElement
    private long cromoteValue;
    @XmlElement
    private int reduceCondition;
    @XmlElement
    private double reduceValue;
    @XmlElement
    private long min;

}
