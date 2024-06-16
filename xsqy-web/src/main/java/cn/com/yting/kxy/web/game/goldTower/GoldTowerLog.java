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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "gold_tower_log")
@Data
@WebMessageType
public class GoldTowerLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "status_id")
    private long statusId;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "finish_floor")
    private long finishFloor;
    @Column(name = "remain_challenge_count")
    private long remainChallengeCount;
    @Column(name = "gain_yb")
    private long gainYb;

}
