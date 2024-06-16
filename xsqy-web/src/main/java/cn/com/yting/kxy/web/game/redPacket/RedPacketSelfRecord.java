/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "red_packet_self_record")
@Data
public class RedPacketSelfRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "total_cost")
    private long totalCost;
    @Column(name = "total_gain")
    private long totalGain;
    @Column(name = "not_take_amount")
    private long notTakeAmount;
    @Column(name = "last_take_amount")
    private long lastTakeAmount;
    @Column(name = "not_lucky_star_combo")
    private long notLuckyStarCombo;

}
