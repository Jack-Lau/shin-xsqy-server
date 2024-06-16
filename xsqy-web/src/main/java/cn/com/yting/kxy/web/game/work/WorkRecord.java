/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.work;

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
 * @author Administrator
 */
@Entity
@Table(name = "work_record")
@Data
public class WorkRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "is_working")
    private boolean isWorking;
    @Column(name = "working_minutes")
    private long workingMinutes;
    @Column(name = "exp_award")
    private long expAward;
    @Column(name = "gold_award")
    private long goldAward;
    @Column(name = "last_update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTime = new Date(0);

}
