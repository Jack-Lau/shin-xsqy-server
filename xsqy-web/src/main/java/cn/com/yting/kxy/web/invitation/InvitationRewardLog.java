/*
 * Created 2018-7-10 12:48:06
 */
package cn.com.yting.kxy.web.invitation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "invitation_reward_log", indexes = @Index(columnList = "account_id, event_time"))
@Data
@WebMessageType
public class InvitationRewardLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "invitee_id", nullable = false)
    private long inviteeId;
    @Column(name = "kbdzp_energy_reward", nullable = false)
    private int kbdzpEnergyReward;
    @Column(name = "kuaibi_reward", nullable = false)
    private int kuaibiReward;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
}
