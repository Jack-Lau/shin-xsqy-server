/*
 * Created 2018-11-12 16:48:29
 */
package cn.com.yting.kxy.web.auction;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "commodity")
@Data
@WebMessageType
public class Commodity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @Column(name = "queue_number", nullable = false)
    private int queueNumber;
    @Column(name = "commodity_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommodityStatus commodityStatus;
    @Column(name = "last_bid", nullable = false)
    private long lastBid;
    @Column(name = "last_bidder_account_id")
    private Long lastBidderAccountId;
    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @Column(name = "broadcast_published", nullable = false)
    private boolean broadcastPublished = false;
    @Column(name = "delivered", nullable = false)
    private boolean delivered = false;

    public CommodityDetail toDetail(CommodityPlayerRepository commodityPlayerRepository) {
        return new CommodityDetail(
            this,
            commodityPlayerRepository.sumLikeCountByCommodityId(id),
            commodityPlayerRepository.countBidderByCommodityId(id)
        );
    }
}
