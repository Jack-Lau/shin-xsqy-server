/*
 * Created 2018-10-27 18:11:06
 */
package cn.com.yting.kxy.web.equipment.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class ItemDisplay implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long modelId;
    @XmlElement
    private long type;
    @XmlElement
    private long prefabId;
    @XmlElement
    private long icon;
    @XmlElement
    private long model;
    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private long showBorderEffect;
    @XmlElement
    private long priority;
    @XmlElement
    private long disappear;

}
