/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "treasure_bowl_attend_record")
@Data
public class TreasureBowlAttendRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "treasure_bowl_id")
    private long treasureBowlId;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "player_name")
    private String playerName;
    @Column(name = "total_changle_token")
    private long totalChangleToken;
    @Column(name = "total_contribution")
    private long totalContribution;
    @Column(name = "total_award")
    private long totalAward;

}
