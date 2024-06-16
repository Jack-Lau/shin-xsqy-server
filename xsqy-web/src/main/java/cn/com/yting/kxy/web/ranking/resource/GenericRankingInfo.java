/*
 * Created 2018-10-31 10:54:09
 */
package cn.com.yting.kxy.web.ranking.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resetting.Resetable;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GenericRankingInfo implements Resource, Resetable {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int quantityRestriction;
    @XmlElement
    private int triggerPoint;
    @XmlElement
    private int reset;
    @XmlElement
    private long awardModel;
    @XmlElement
    private long mail;

    @Override
    public ResetType getResetType() {
        switch (triggerPoint) {
            case 1:
                return ResetType.DAILY;
            case 2:
                return ResetType.WEEKLY;
            default:
                return ResetType.NEVER;
        }
    }

}
