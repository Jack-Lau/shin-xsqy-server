/*
 * Created 2018-11-2 15:35:30
 */
package cn.com.yting.kxy.web.game.treasure.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.random.pool.PoolSelectorElement;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class TreasureAward implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private long currencyId;
    @XmlElement
    private long amount;
    @XmlElement
    private double probability;
    @XmlElement
    private long broadcastId;
    @XmlElement
    private long interfaceBroadcastId;

    public PoolSelectorElement toPoolSelectorElement() {
        return new PoolSelectorElement(currencyId, amount, probability, this);
    }
}
