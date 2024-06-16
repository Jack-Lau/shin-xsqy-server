/*
 * Created 2018-8-8 17:11:13
 */
package cn.com.yting.kxy.core.parameter.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class Attributes implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int show;
    @XmlElement
    private double basicValueOfCharacter;
    @XmlElement
    private double basicValueOfPet;
    @XmlElement
    private double defaultValueOfMonster;

    public boolean isExposable() {
        return show == 1;
    }
}
