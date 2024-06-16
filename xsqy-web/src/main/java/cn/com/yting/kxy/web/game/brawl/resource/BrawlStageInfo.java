/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class BrawlStageInfo implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long awardAmount;

    @XmlElement
    private double minConstantOne;
    @XmlElement
    private double minConstantTwo;
    @XmlElement
    private double maxConstantOne;
    @XmlElement
    private double maxConstantTwo;
    @XmlElement
    private long extraEnergyAward;

}
