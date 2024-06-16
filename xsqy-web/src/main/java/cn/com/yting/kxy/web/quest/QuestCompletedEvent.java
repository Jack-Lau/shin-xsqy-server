/*
 * Created 2018-8-2 16:06:30
 */
package cn.com.yting.kxy.web.quest;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class QuestCompletedEvent extends KxyWebEvent {

    private final QuestRecord questRecord;

    public QuestCompletedEvent(Object source, QuestRecord questRecord) {
        super(source);
        this.questRecord = questRecord;
    }

    public QuestRecord getQuestRecord() {
        return questRecord;
    }
}
