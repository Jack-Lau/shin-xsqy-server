/*
 * Created 2018-8-1 15:25:39
 */
package cn.com.yting.kxy.web.quest;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.quest.QuestRecord.QuestRecordPK;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "quest_record", indexes = @Index(columnList = "quest_id"))
@IdClass(QuestRecordPK.class)
@Data
@WebMessageType
public class QuestRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "quest_id")
    private long questId;
    @Column(name = "quest_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestStatus questStatus = QuestStatus.NOT_STARTED_YET;
    @Column(name = "results", nullable = false)
    private String results = "";
    @Column(name = "objective_status", nullable = false)
    private String objectiveStatus;
    @Column(name = "random_bac_id")
    private Long randomBacId;
    @Column(name = "started_count", nullable = false)
    private int startedCount = 0;

    public void appendResult(String result) {
        results += result;
    }

    public boolean isObjectiveCompleted(int index) {
        return objectiveStatus.charAt(index) == 'T';
    }

    public void setObjectiveCompleted(int index) {
        char[] chars = objectiveStatus.toCharArray();
        chars[index] = 'T';
        objectiveStatus = new String(chars);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestRecordPK implements Serializable {

        private long accountId;
        private long questId;

    }

}
