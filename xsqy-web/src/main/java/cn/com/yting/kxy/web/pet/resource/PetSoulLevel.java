/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.pet.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class PetSoulLevel implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private double 外伤;
    @XmlElement
    private double 内伤;
    @XmlElement
    private double 外防;
    @XmlElement
    private double 内防;
    @XmlElement
    private double 气血;
    @XmlElement
    private double 幸运;
    @XmlElement
    private double 速度;
    @XmlElement
    private double 招式;
    @XmlElement
    private double 抵抗;
    @XmlElement
    private double 连击;
    @XmlElement
    private double 吸血;
    @XmlElement
    private double 暴击;
    @XmlElement
    private double 暴效;
    @XmlElement
    private double 招架;
    @XmlElement
    private double 神佑;
    @XmlElement
    private long fc;
    @XmlElement
    private long purple_exp;
    @XmlElement
    private long orange_exp;

    public double getParameterBySoulName(String soulName) {
        switch (soulName) {
            case "外伤":
                return 外伤;
            case "内伤":
                return 内伤;
            case "外防":
                return 外防;
            case "内防":
                return 内防;
            case "气血":
                return 气血;
            case "幸运":
                return 幸运;
            case "速度":
                return 速度;
            case "招式":
                return 招式;
            case "抵抗":
                return 抵抗;
            case "连击":
                return 连击;
            case "吸血":
                return 吸血;
            case "暴击":
                return 暴击;
            case "暴效":
                return 暴效;
            case "招架":
                return 招架;
            case "神佑":
                return 神佑;
        }
        return 0;
    }

}
