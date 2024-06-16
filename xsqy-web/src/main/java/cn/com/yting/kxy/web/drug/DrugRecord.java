/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug;

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
import lombok.Data;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "drug_record")
@Data
public class DrugRecord implements Serializable, LongId {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "drug_id")
    private long drugId;
    @Column(name = "value_percent_1")
    private double valuePercent_1;
    @Column(name = "value_percent_2")
    private double valuePercent_2;
    @Column(name = "expire_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireTime = new Date(0);
    
}
