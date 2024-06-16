/*
 * Created 2018-10-30 15:54:47
 */
package cn.com.yting.kxy.web.ranking;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "ranking_record", indexes = {
    // 中间可能为 null 的索引没有意义
    // 索引序必须保持一致，所以需要降序的 ranking_value 使用负值来存储
    @Index(columnList = "ranking_id,ranking_value_1,ranking_value_2,ranking_value_3,ranking_value_4,ranking_value_5,last_modified,account_id")
})
@Data
@IdClass(RankingRecord.PK.class)
@WebMessageType
public class RankingRecord implements Serializable {

    @Id
    @Column(name = "ranking_id")
    private long rankingId;
    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "object_id")
    private long objectId;

    @Column(name = "ranking_value_1", nullable = false)
    private long rankingValue_1;
    @Column(name = "ranking_value_2", nullable = false)
    private long rankingValue_2;
    @Column(name = "ranking_value_3", nullable = false)
    private long rankingValue_3;
    @Column(name = "ranking_value_4", nullable = false)
    private long rankingValue_4;
    @Column(name = "ranking_value_5", nullable = false)
    private long rankingValue_5;

    @Column(name = "last_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    /**
     * 尝试更新排行值，如果给出的值与当前值不同，则会更新 lastModified
     *
     * @param values
     * @param currentTime
     */
    public void tryUpdateRankingValues(RankingValues values, Date currentTime) {
        if (rankingValue_1 != values.getRankingValue_1()
            || rankingValue_2 != values.getRankingValue_2()
            || rankingValue_3 != values.getRankingValue_3()
            || rankingValue_4 != values.getRankingValue_4()
            || rankingValue_5 != values.getRankingValue_5()) {
            rankingValue_1 = values.getRankingValue_1();
            rankingValue_2 = values.getRankingValue_2();
            rankingValue_3 = values.getRankingValue_3();
            rankingValue_4 = values.getRankingValue_4();
            rankingValue_5 = values.getRankingValue_5();
            lastModified = currentTime;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long rankingId;
        private long accountId;
        private long objectId;
    }
}
