/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "gold_tower_challenge")
@Data
@WebMessageType
public class GoldTowerChallengeEntity implements Serializable, Comparable<GoldTowerChallengeEntity> {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "available_challenge_count", nullable = false)
    private int availableChallengeCount;
    @Column(name = "is_in_challenge", nullable = false)
    private boolean isInChallenge;
    @Column(name = "last_floor_count", nullable = false)
    private long lastFloorCount;
    @Column(name = "finish_last_floor_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishLastFloorTime;

    @Column(name = "current_room_id", nullable = false)
    private long currentRoomId;
    @Column(name = "current_battle_session_id", nullable = false)
    private long currentBattleSessionId;
    @Column(name = "is_current_room_challenge_success", nullable = false)
    private boolean isCurrentRoomChallengeSuccess;
    @Column(name = "available_treasure_count", nullable = false)
    private int availableTreasureCount;

    @Override
    public int compareTo(GoldTowerChallengeEntity t) {
        if (this.lastFloorCount > t.lastFloorCount) {
            return 1;
        }
        if (this.lastFloorCount < t.lastFloorCount) {
            return -1;
        }
        if (this.finishLastFloorTime.before(t.finishLastFloorTime)) {
            return 1;
        }
        return -1;
    }

}
