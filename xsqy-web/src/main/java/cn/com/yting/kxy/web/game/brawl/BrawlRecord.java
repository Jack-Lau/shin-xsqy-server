/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "brawl_record")
@Data
public class BrawlRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "reset_count")
    private long resetCount;

    @Column(name = "brawl_count")
    private long brawlCount;
    @Column(name = "current_stage")
    private long currentStage;
    @Column(name = "current_battle_session_id")
    private long currentBattleSessionId;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BrawlStatus status;

    @Column(name = "team_member_1")
    private long teamMember_1;
    @Column(name = "team_member_2")
    private long teamMember_2;
    @Column(name = "team_max_fc")
    private Long teamMaxFc;

}
