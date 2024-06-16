/*
 * Created 2018-10-19 18:02:39
 */
package cn.com.yting.kxy.web.game.minearena;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "mine_arena_award_obtain_log", indexes = @Index(columnList = "account_id,event_time"))
@Data
public class MineArenaRewardObtainLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "reward_text", nullable = false)
    @JsonIgnore
    private String rewardText;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;

    public List<CurrencyStack> getRewardStacks() {
        return CurrencyStack.listFromText(rewardText);
    }
}
