/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

import cn.com.yting.kxy.web.repository.LongId;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "antique_shared_record")
@Data
public class AntiqueSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "public_award_account_id")
    private Long publicAwardAccountId;
    @Column(name = "public_award_remain_count")
    private int publicAwardRemainCount;
    @Column(name = "last_public_award_create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPublicAwardCreateTime;

}
