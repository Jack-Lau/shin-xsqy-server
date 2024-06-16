/*
 * Created 2018-11-9 17:08:13
 */
package cn.com.yting.kxy.web.currency.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class CurrencyToConsumables implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int effectID;
    @XmlElement
    private long effectParameter;
    @XmlElement
    private int conditionID;
    @XmlElement
    private long conditionParameter;
    @XmlElement
    private int extraID;
    @XmlElement
    private long extraParameter;

}
