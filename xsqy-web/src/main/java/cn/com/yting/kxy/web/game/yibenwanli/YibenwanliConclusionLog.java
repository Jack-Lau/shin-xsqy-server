/*
 * Created 2018-9-6 16:59:08
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "yibenwanli_conclusion_log")
@Data
public class YibenwanliConclusionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "final_pool", nullable = false)
    private long finalPool;
    @Column(name = "total_ticket_count", nullable = false)
    private long totalTicketCount;
    @Column(name = "award_for_last_one", nullable = false)
    private long awardForLastOne;
    @Column(name = "award_per_ticket", nullable = false)
    private long awardPerTicket;
    @Column(name = "last_purchase_account_id", nullable = false)
    private long lastPurchaseAccountId;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Column(name = "award_for_lucky_one", nullable = false)
    private long awardForLuckyOne;
    @Column(name = "lucky_one_account_id", nullable = false)
    private long luckyOneAccountId;

}
