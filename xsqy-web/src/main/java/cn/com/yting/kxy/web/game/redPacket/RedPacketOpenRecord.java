/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

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
@Table(name = "red_packet_open_record")
@Data
public class RedPacketOpenRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "red_packet_id")
    private long redPacketId;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "player_name")
    private String playerName;
    @Column(name = "gain_amount")
    private long gainAmount;
    @Column(name = "lucky_star")
    private boolean luckyStar;

}
