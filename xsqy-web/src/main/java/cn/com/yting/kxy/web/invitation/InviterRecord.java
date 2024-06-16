/*
 * Created 2018-7-9 15:33:24
 */
package cn.com.yting.kxy.web.invitation;

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
@Table(name = "inviter_record")
@Data
@WebMessageType
public class InviterRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "invitation_code", nullable = false, unique = true)
    private String invitationCode;
    @Column(name = "invitation_limit", nullable = false)
    private int invitationLimit = InvitationConstants.DEFAULT_INVITATION_LIMIT;
    @Column(name = "last_reward_resolve_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRewardResolveTime = new Date();
    @Column(name = "today_kbdzp_energy_reward", nullable = false)
    private int todayKbdzpEnergyReward = 0;
    @Column(name = "today_kuaibi_reward", nullable = false)
    private int todayKuaibiReward = 0;
    @Column(name = "today_reward_delivered", nullable = false)
    private boolean todayRewardDelivered = true;

    public void increaseInvitationLimit() {
        invitationLimit++;
    }
}
