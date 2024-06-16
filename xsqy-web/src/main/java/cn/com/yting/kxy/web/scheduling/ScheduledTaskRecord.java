/*
 * Created 2018-7-3 19:19:31
 */
package cn.com.yting.kxy.web.scheduling;

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
@Table(name = "scheduled_task_record")
@Data
public class ScheduledTaskRecord implements Serializable {

    @Id
    @Column(name = "task_name")
    private String taskName;
    @Column(name = "last_execution", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastExecution;

    public ScheduledTaskRecord() {
    }

    public ScheduledTaskRecord(String taskName) {
        this.taskName = taskName;
    }
}
