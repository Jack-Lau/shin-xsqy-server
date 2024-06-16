/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

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
@Table(name = "baccarat_game")
@Data
@WebMessageType
public class BaccaratGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "red_point_1")
    private long redPoint_1;
    @Column(name = "red_point_2")
    private long redPoint_2;
    @Column(name = "blue_point_1")
    private long bluePoint_1;
    @Column(name = "blue_point_2")
    private long bluePoint_2;
    @Column(name = "lottery_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lotteryTime;

}
