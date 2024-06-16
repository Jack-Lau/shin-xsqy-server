/*
 * Created 2018-11-20 12:40:45
 */
package cn.com.yting.kxy.web.impartation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "disciple_record", indexes = @Index(columnList = "master_account_id"))
@Data
@WebMessageType
public class DiscipleRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "master_account_id", nullable = false)
    private long masterAccountId;
    @Column(name = "discipline_phase", nullable = false)
    @Enumerated(EnumType.STRING)
    private DisciplinePhase phase;
    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Column(name = "deadline", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date deadline;
    @Column(name = "daily_practice_generated", nullable = false)
    private boolean dailyPracticeGenerated;
    @Column(name = "disciple_confirmed", nullable = false)
    private boolean discipleConfirmed;
    @Column(name = "master_confirmed", nullable = false)
    private boolean masterConfirmed;
    @Column(name = "confirmation_date")
    @Temporal(TemporalType.DATE)
    private Date confirmationDate;
    @Column(name = "huoyue_pool", nullable = false)
    private long huoyuePool;
    @Column(name = "disciple_last_huoyue_delivery")
    @Temporal(TemporalType.TIMESTAMP)
    private Date discipleLastHuoyueDelivery;
    @Column(name = "master_last_huoyue_delivery")
    @Temporal(TemporalType.TIMESTAMP)
    // typo
    private Date masterLastHuoyueDelivery;
    @Column(name = "player_level_at_midnight", nullable = false)
    private int playerLevelAtMidnight;
    @Column(name = "yesterday_contribution_pool", nullable = false)
    private long yesterdayContributionPool;
    @Column(name = "today_contribution_pool", nullable = false)
    private long todayContributionPool;
    @Column(name = "yesterday_exp_pool", nullable = false)
    private long yesterdayExpPool;
    @Column(name = "today_exp_pool", nullable = false)
    private long todayExpPool;
    @Column(name = "last_contribution_exp_delivery", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastContributionExpDelivery;

    public void increaseTodayContributionPool(long value) {
        todayContributionPool += value;
    }

    public void increaseTodayExpPool(long value) {
        todayExpPool += value;
    }
}
