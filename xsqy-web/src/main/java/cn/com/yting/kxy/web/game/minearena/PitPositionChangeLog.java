/*
 * Created 2018-10-17 17:04:50
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
@Table(name = "pit_position_change_log", indexes = @Index(columnList = "account_id,event_time"))
@Data
public class PitPositionChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    /**
     * 从没有拥有任何位置变为一个特定的位置时，此值应为 {@link MineArenaConstants#POSITION_INIT}
     */
    @Column(name = "before_position", nullable = false)
    private long beforePosition = MineArenaConstants.POSITION_INIT;
    @Column(name = "after_position", nullable = false)
    private long afterPosition;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
}
