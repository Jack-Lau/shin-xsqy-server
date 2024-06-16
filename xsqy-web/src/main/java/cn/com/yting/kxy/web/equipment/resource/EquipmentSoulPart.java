/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class EquipmentSoulPart implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int part;
    @XmlElement
    private String name_1;
    @XmlElement
    private double probability_1;
    @XmlElement
    private String name_2;
    @XmlElement
    private double probability_2;
    @XmlElement
    private String name_3;
    @XmlElement
    private double probability_3;
    @XmlElement
    private String name_4;
    @XmlElement
    private double probability_4;
    @XmlElement
    private String name_5;
    @XmlElement
    private double probability_5;

}
