/*
 * Created 2018-11-2 12:45:33
 */
package cn.com.yting.kxy.web.awardpool;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "award_pool_player_record")
@IdClass(AwardPoolPlayerRecord.PK.class)
@Data
public class AwardPoolPlayerRecord implements Serializable {

    @Id
    @Column(name = "pool_id")
    private long poolId;
    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "pool_value", nullable = false)
    private long poolValue;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long poolId;
        private long accountId;
    }
}
