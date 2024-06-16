/*
 * Created 2018-10-20 17:24:52
 */
package cn.com.yting.kxy.web.game.minearena;

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

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "mine_arena_challenge_log", indexes = {
    @Index(columnList = "challenger_account_id"),
    @Index(columnList = "defender_account_id")
})
@Data
public class MineArenaChallengeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "challenger_account_id", nullable = false)
    private long challengerAccountId;
    @Column(name = "defender_account_id", nullable = false)
    private long defenderAccountId;
    @Column(name = "cost", nullable = false)
    private long cost;
    @Column(name = "challenger_position", nullable = false)
    private long challengerPosition;
    @Column(name = "defender_position", nullable = false)
    private long defenderPosition;
    @Column(name = "success", nullable = false)
    private boolean success;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
}
