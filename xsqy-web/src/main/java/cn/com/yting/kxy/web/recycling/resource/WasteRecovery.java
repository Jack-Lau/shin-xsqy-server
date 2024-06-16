/*
 * Created 2018-9-20 16:32:56
 */
package cn.com.yting.kxy.web.recycling.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class WasteRecovery implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int type;
    @XmlElement
    private long recoveryId;
    @XmlElement
    private String name;
    @XmlElement
    private long amount;
    @XmlElement
    private double weight;
    @XmlElement
    private int highRate;

}
