/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "gold_tower_record")
@Data
@WebMessageType
public class GoldTowerRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "max_finish_floor")
    private long maxFinishFloor;

    @Column(name = "wipe_out_battle_session_id")
    private Long wipeOutBattleSessionId;
    @Column(name = "wipe_out_battle_win")
    private Boolean wipeOutBattleWin;
    @Column(name = "up_to_target_floor")
    private Boolean upToTargetFloor;
    @Column(name = "taken_wipe_out_award")
    private Boolean takenWipeOutAward;

}
