/*
 * Created 2018-10-17 16:32:14
 */
package cn.com.yting.kxy.web.game.minearena;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "pit", indexes = @Index(columnList = "account_id"))
@Data
@WebMessageType
public class Pit implements Serializable {

    @Id
    @Column(name = "position")
    private long position;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "challenged_count", nullable = false)
    private int challengedCount;

    public void increaseChallengedCount() {
        challengedCount++;
    }
}
