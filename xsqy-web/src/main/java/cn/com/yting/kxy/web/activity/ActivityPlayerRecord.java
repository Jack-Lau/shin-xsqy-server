/*
 * Created 2018-10-16 15:11:54
 */
package cn.com.yting.kxy.web.activity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "activity_player_record")
@Data
public class ActivityPlayerRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "incoming_active_points", nullable = false)
    private long incomingActivePoints;

    public void increaseIncomingActivePoints(long value) {
        incomingActivePoints += value;
    }
}
