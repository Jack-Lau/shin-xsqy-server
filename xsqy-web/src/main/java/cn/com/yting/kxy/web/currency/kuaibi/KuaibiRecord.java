/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.currency.kuaibi;

import cn.com.yting.kxy.web.repository.LongId;
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
@Data
@Table(name = "kuaibi_record")
@WebMessageType
public class KuaibiRecord implements Serializable, LongId {

    @Id
    private Long id;
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
