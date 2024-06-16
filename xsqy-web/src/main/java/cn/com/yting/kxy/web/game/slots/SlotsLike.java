/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "slots_like")
@Data
@WebMessageType
public class SlotsLike implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "big_prize_id")
    private long bigPrizeId;
    @Column(name = "sender_id")
    private long senderId;
    @Column(name = "receiver_id")
    private long receiverId;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

}
