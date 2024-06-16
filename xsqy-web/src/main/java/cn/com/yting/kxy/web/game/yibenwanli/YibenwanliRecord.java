/*
 * Created 2018-9-3 15:54:08
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "yibenwanli_record")
@Data
@WebMessageType
public class YibenwanliRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "ticket_count", nullable = false)
    private int ticketCount;
    @Column(name = "last_purchase_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPurchaseTime = new Date(0);

    public void increaseTicketCount() {
        ticketCount++;
    }
}
