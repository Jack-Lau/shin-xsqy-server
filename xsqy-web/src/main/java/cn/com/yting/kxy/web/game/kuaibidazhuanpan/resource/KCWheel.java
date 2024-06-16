/*
 * Created 2018-7-13 11:26:55
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class KCWheel implements Resource {

    @XmlAttribute
    long id;
    @XmlElement
    String name;
    @XmlElement
    long currencyId;
    @XmlElement
    long amount;
    @XmlElement
    double normalProbability;
    @XmlElement
    double lowLevelProbability;
    @XmlElement
    Long broadcastId;
    @XmlElement
    int sequence;
    @XmlElement
    int exhibit;
    @XmlElement
    int showResult;

    public boolean isNeedToShowResult() {
        return showResult == 1;
    }
}
