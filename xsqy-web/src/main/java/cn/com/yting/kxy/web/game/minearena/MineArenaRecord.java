/*
 * Created 2018-10-17 18:23:41
 */
package cn.com.yting.kxy.web.game.minearena;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "mine_arena_record")
@Data
@WebMessageType
public class MineArenaRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "challenge_point")
    private int challengePoint = MineArenaConstants.CHALLENGE_POINT_INIT;
    @Column(name = "last_reward_resolve_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRewardResolveTime = new Date();
    @Column(name = "resolved_reward", nullable = false)
    @JsonIgnore
    private String resolvedReward = "";
    @Column(name = "resolved_reward_delivered", nullable = false)
    private boolean resolvedRewardDelivered = true;

    public void decreaseChallengePoint(int value) {
        if (value > challengePoint) {
            throw new IllegalArgumentException("挑战点不足以减少");
        }
        challengePoint -= value;
    }

    public List<CurrencyStack> getResolvedRewardStacks() {
        return CurrencyStack.listFromText(resolvedReward);
    }
}
