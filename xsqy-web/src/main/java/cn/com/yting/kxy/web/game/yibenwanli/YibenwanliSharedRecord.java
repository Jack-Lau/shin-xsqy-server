/*
 * Created 2018-9-1 17:45:33
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.repository.LongId;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "yibenwanli_shared_record")
@Data
public class YibenwanliSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "pool", nullable = false)
    private long pool = 0;
    @Column(name = "total_ticket_count", nullable = false)
    private int totalTicketCount = 0;
    @Column(name = "last_purchase_account_id")
    private Long lastPurchaseAccountId;
    @Column(name = "deadline_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadlineTime;
    @Column(name = "paused_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pausedTime;
    @Column(name = "closed", nullable = false)
    private boolean closed = true;
    @Column(name = "next_season_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextSeasonTime;
    @Column(name = "next_season_init_pool", nullable = false)
    private long nextSeasonInitPool;

    public void pushBackDeadline(Instant currentInstant, Duration duration) {
        Instant deadlineInstant = deadlineTime.toInstant();
        deadlineInstant = deadlineInstant.plus(duration);
        if (Duration.between(currentInstant, deadlineInstant).compareTo(YibenwanliConstants.DURATION_MAX_TO_DEADLINE) > 0) {
            deadlineInstant = currentInstant.plus(YibenwanliConstants.DURATION_MAX_TO_DEADLINE);
        }
        deadlineTime = Date.from(deadlineInstant);
    }

    public void addToPool(long value) {
        pool += value;
    }

    public void increaseTotalTicketCount() {
        totalTicketCount++;
    }

    public long getTicketPrice() {
        return (long) Math.pow((double) totalTicketCount / 10.0, 2.5) + 10;
    }

    public double getLastShotRate(Instant currentInstant) {
        Instant deadlineInstant = deadlineTime.toInstant();
        return 9.0 / ((double) Duration.between(currentInstant, deadlineInstant).toMinutes() + 120.0) - 0.0029;
    }

}
