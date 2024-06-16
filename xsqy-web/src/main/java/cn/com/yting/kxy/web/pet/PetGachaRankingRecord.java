/*
 * Created 2018-10-12 15:32:09
 */
package cn.com.yting.kxy.web.pet;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "pet_gacha_ranking_record", indexes = @Index(columnList = "point desc, last_modified asc"))
@Data
@WebMessageType
public class PetGachaRankingRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "point", nullable = false)
    private long point;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified", nullable = false)
    private Date lastModified;
}
