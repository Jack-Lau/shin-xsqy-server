/*
 * Created 2018-11-2 12:44:17
 */
package cn.com.yting.kxy.web.awardpool;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "award_pool_record")
@Data
public class AwardPoolRecord implements Serializable {

    @Id
    private long id;
    @Column(name = "pool_value", nullable = false)
    private long poolValue;
}
