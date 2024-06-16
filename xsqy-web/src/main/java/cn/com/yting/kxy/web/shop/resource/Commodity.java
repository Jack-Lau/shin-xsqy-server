/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop.resource;

import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class Commodity implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long currencyId;
    @XmlElement
    private long purchaseCurrencyId;
    @XmlElement
    private String unitPrice;
    @XmlElement
    private int allowBatchBuy;
    @XmlElement
    private int resetCondition;
    @XmlElement
    private int replenishCondition;
    @XmlElement
    private long replenishAmount;
    @XmlElement
    private long replenishUpperLimit;

}
