/*
 * Created 2018-10-24 11:20:06
 */
package cn.com.yting.kxy.web.pet.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class PetAbilityStudy implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int abilityAmount;
    @XmlElement
    private double successRate;
    @XmlElement
    private long currencyId;
    @XmlElement
    private String currencyName;
    @XmlElement
    private long amount;
}
