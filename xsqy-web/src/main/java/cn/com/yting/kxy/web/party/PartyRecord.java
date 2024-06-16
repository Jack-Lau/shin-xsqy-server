/*
 * Created 2018-9-26 10:57:20
 */
package cn.com.yting.kxy.web.party;

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
@Table(name = "party_record")
@Data
@WebMessageType
public class PartyRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "candidate_supporters", length = 1000, nullable = false)
    private String candidateSupporters = "";
    @Column(name = "high_level_candidate", nullable = false)
    private boolean highLevelCandidate;
    @Column(name = "last_reward_resolve_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRewardResolveTime = new Date();
    @Column(name = "support_reward", nullable = false)
    private long supportReward;
    @Column(name = "today_reward_delivered", nullable = false)
    private boolean todayRewardDelivered = true;

}
