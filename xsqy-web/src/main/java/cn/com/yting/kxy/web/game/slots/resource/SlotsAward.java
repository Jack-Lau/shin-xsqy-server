/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class SlotsAward implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String awardName;
    @XmlElement
    private long award;
    @XmlElement
    private long interfaceBroadcast;
    @XmlElement
    private int topPrize;

}
