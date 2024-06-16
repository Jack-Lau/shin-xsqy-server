/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class GoldTowerRoomPrototype implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long color;
    @XmlElement
    private int challengeType;
    @XmlElement
    private String challengeParam_1;
    @XmlElement
    private String challengeParam_2;
    @XmlElement
    private String challengeParam_3;

}
