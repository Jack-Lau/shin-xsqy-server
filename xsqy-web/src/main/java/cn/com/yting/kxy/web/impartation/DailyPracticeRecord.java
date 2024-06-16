/*
 * Created 2018-11-21 17:38:11
 */
package cn.com.yting.kxy.web.impartation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "daily_practice_record", indexes = @Index(columnList = "definition_id"))
@IdClass(DailyPracticeRecord.PK.class)
@Data
public class DailyPracticeRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "definition_id")
    private long definitionId;
    @Column(name = "daily_practices_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DailyPracticeStatus status;
    @Column(name = "progress", nullable = false)
    private int progress;

    public void increaseProgress() {
        progress++;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PK implements Serializable {

        private long accountId;
        private long definitionId;
    }
}
