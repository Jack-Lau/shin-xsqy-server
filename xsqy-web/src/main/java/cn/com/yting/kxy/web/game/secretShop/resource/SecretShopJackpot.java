/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class SecretShopJackpot implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long serialNumber;
    @XmlElement
    private long currencyId;
    @XmlElement
    private String name;
    @XmlElement
    private long currencyAmount;
    @XmlElement
    private double probability;
    @XmlElement
    private int limit;
    @XmlElement
    private long broadcastId;

}
