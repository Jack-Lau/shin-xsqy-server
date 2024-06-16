/*
 * Created 2018-10-13 11:34:33
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
// c宠物冲星表.xlsx revision 11408
@Getter
public class PetAddStar implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int starLevel;
    @XmlElement
    private int starStage;
    @XmlElement
    private double rate;
    @XmlElement
    private double pomotion;
    @XmlElement
    private long currencyId;
    @XmlElement
    private String name;
    @XmlElement
    private long amount;

}
