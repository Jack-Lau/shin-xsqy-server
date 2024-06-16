/*
 * Created 2018-12-12 11:43:57
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

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
@Table(name = "mjdh_battle_log", indexes = {
    @Index(columnList = "winner_account_id, event_time"),
    @Index(columnList = "loser_account_id, event_time")
})
@Data
@WebMessageType
public class MjdhBattleLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Column(name = "winner_account_id", nullable = false)
    private long winnerAccountId;
    @Column(name = "loser_account_id", nullable = false)
    private long loserAccountId;
    @Column(name = "winner_before_grade", nullable = false)
    private int winnerBeforeGrade;
    @Column(name = "winner_after_grade", nullable = false)
    private int winnertAftereGrade;
    @Column(name = "loser_before_grade", nullable = false)
    private int loserBeforeGrade;
    @Column(name = "loser_after_grade", nullable = false)
    private int loserAfterGrade;
}
