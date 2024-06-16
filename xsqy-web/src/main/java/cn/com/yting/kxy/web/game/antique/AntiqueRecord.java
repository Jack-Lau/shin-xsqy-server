/*
 * Created 2018-11-5 18:28:06
 */
package cn.com.yting.kxy.web.game.antique;

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
 * @author Azige
 */
@Entity
@Table(name = "antique_record")
@Data
public class AntiqueRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "started", nullable = false)
    private boolean started;
    @Column(name = "repair_count", nullable = false)
    private int repairCount;
    @Column(name = "progress", nullable = false)
    private int progress;
    @Column(name = "part")
    private String part;
    @Column(name = "last_public_award_obtain_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPublicAwardObtainTime;
    @Column(name = "public_award_obtain_count", nullable = false)
    private int publicAwardObtainCount;

}
