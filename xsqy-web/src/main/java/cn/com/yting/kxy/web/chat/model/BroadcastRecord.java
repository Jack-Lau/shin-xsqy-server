/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.chat.model;

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
@Table(name = "broadcast_record")
@Data
public class BroadcastRecord implements Serializable {

    @Id
    @Column(name = "broadcast_id")
    private long broadcastId;
    @Column(name = "today_send_count")
    private int todaySendCount;

}
