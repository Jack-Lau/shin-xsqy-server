/*
 * Created 2018-10-16 15:10:59
 */
package cn.com.yting.kxy.web.activity;

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
@Table(name = "activity_record")
@Data
@IdClass(ActivityRecord.PK.class)
public class ActivityRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "activity_id")
    private long activityId;
    @Column(name = "progress", nullable = false)
    private int progress;
    @Column(name = "completed", nullable = false)
    private boolean completed;

    public void increaseProgress() {
        progress++;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PK implements Serializable {

        private long accountId;
        private long activityId;
    }
}
