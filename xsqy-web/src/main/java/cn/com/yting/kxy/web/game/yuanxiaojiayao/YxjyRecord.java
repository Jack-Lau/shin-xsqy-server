/*
 * Created 2019-2-13 15:51:21
 */
package cn.com.yting.kxy.web.game.yuanxiaojiayao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.core.util.PropertyList;
import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "yxjy_record")
@Data
@WebMessageType
public class YxjyRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "award_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private YxjyAwardSatatus awardSatatus = YxjyAwardSatatus.NOT_AVAILABLE;
    @Column(name = "last_invitation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastInvitationTime;
    @Column(name = "invited_account_id_1")
    @JsonIgnore
    private Long invitedAccountId_1;
    @Column(name = "invited_account_id_2")
    @JsonIgnore
    private Long invitedAccountId_2;
    @Column(name = "invited_account_id_3")
    @JsonIgnore
    private Long invitedAccountId_3;
    @Column(name = "today_attended_count", nullable = false)
    private int todayAttendedCount;
    @Column(name = "attended_account_id_1")
    @JsonIgnore
    private Long attendedAccountId_1;
    @Column(name = "attended_account_id_2")
    @JsonIgnore
    private Long attendedAccountId_2;
    @Column(name = "attended_account_id_3")
    @JsonIgnore
    private Long attendedAccountId_3;
    @Column(name = "attended_account_id_4")
    @JsonIgnore
    private Long attendedAccountId_4;
    @Column(name = "attended_account_id_5")
    @JsonIgnore
    private Long attendedAccountId_5;

    private transient List<Long> invitedAccountIds = PropertyList.<Long>builder()
        .add(this::getInvitedAccountId_1, this::setInvitedAccountId_1)
        .add(this::getInvitedAccountId_2, this::setInvitedAccountId_2)
        .add(this::getInvitedAccountId_3, this::setInvitedAccountId_3)
        .build();

    private transient List<Long> attendedAccountIds = PropertyList.<Long>builder()
        .add(this::getAttendedAccountId_1, this::setAttendedAccountId_1)
        .add(this::getAttendedAccountId_2, this::setAttendedAccountId_2)
        .add(this::getAttendedAccountId_3, this::setAttendedAccountId_3)
        .add(this::getAttendedAccountId_4, this::setAttendedAccountId_4)
        .add(this::getAttendedAccountId_5, this::setAttendedAccountId_5)
        .build();

    public void increaseTodayAttendedCount() {
        todayAttendedCount++;
    }
}
