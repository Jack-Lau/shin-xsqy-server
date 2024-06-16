/*
 * Created 2018-11-13 17:34:16
 */
package cn.com.yting.kxy.web.auction.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
// h黑市拍卖品表.xlsx revision 11923
@Getter
public class BlackMarketAuctionInfo implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long auctionId;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private long floorPrice;
    @XmlElement
    private long PriceRise;
    @XmlElement
    private long time;
    @XmlElement
    private long broadcastCondition;
    @XmlElement
    private long broadcastId;
    @XmlElement
    private double probability;

}
