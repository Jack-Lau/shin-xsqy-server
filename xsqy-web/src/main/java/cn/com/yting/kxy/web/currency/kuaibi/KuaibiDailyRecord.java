/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.currency.kuaibi;

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

import cn.com.yting.kxy.web.repository.LongId;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Data
@Table(name = "kuaibi_daily_record")
@WebMessageType
public class KuaibiDailyRecord implements Serializable, LongId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "maintenance_milli_kuaibi", nullable = false)
    private long maintenanceMilliKuaibi;
    @Column(name = "destroy_milli_kuaibi", nullable = false)
    private long destroyMilliKuaibi;
    @Column(name = "rebate_milli_kuaibi_from_player_interactive", nullable = false)
    private long rebateMilliKuaibiFromPlayerInteractive;
    @Column(name = "rebate_milli_kuaibi_from_other", nullable = false)
    private long rebateMilliKuaibiFromOther;
    @Column(name = "airdrop_milli_kuaibi", nullable = false)
    private long airdropMilliKuaibi;

}
