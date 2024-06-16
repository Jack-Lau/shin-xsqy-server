/*
 * Created 2018-11-15 15:32:41
 */
package cn.com.yting.kxy.web.auction;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.repository.LongId;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "auction_shared_record")
@Data
public class AuctionSharedRecord implements LongId, Serializable {

    @Id
    private Long id;
    @Column(name = "paused_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pausedTime;
}
