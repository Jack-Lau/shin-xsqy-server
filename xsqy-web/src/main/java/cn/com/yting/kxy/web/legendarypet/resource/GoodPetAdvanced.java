/*
 * Created 2019-1-23 18:05:15
 */
package cn.com.yting.kxy.web.legendarypet.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GoodPetAdvanced implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int name;
    @XmlElement
    private long advancedId;
    @XmlElements(@XmlElement(name = "cost", type = Cost.class))
    private List<Cost> costs;
    @XmlElement
    private long getAbility;
    @XmlElement
    private int promoteAptitude;

    @Getter
    public static class Cost {

        @XmlElement
        private long currencyId;
        @XmlElement
        private long amount;

    }
}
