/*
 * Created 2018-7-6 16:41:05
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "kbdzp_record")
@Data
@WebMessageType
public class KbdzpRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "booster1", nullable = false)
    private boolean booster1 = false;
    @Column(name = "booster2", nullable = false)
    private boolean booster2 = false;
    @Column(name = "recover_ref_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date recoverRefTime;
    @Column(name = "invitee_bonus_available", nullable = false)
    private boolean inviteeBonusAvailable = false;
    @Column(name = "invitee_bonus_delivered", nullable = false)
    private boolean inviteeBonusDelivered = false;
    @Column(name = "pending_award")
    private Long pendingAward;
    @Column(name = "kuaibi_pool", nullable = false)
    private long kuaibiPool = KbdzpConstants.PERSONAL_POOL_RESET_VALUE;
    @Column(name = "kuaibi_pool_last_reset", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date kuaibiPoolLastReset;
    @Column(name = "total_gain_milliKC")
    private long totalGainMilliKC;
    @Column(name = "total_turn_count")
    private long totalTurnCount;
    @Column(name = "today_turn_count")
    private long todayTurnCount;

    public KbdzpRecord() {
    }

    public KbdzpRecord(Date currentTime) {
        this.recoverRefTime = currentTime;
        this.kuaibiPoolLastReset = currentTime;
    }

    public void resetKuaibiPoolIfNecessary(long currentTime) {
        LocalDate lastResetDate = TimeUtils.toOffsetTime(kuaibiPoolLastReset).toLocalDate();
        LocalDate today = TimeUtils.toOffsetTime(currentTime).toLocalDate();
        if (lastResetDate.isBefore(today)) {
            kuaibiPool = KbdzpConstants.PERSONAL_POOL_RESET_VALUE;
            kuaibiPoolLastReset = new Date(currentTime);
        }
    }
}
