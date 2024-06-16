/*
 * Created 2018-8-3 11:44:14
 */
package cn.com.yting.kxy.web.quest;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class QuestStartedEvent extends KxyWebEvent {

    private final QuestRecord questRecord;

    public QuestStartedEvent(Object source, QuestRecord questRecord) {
        super(source);
        this.questRecord = questRecord;
    }

    public QuestRecord getQuestRecord() {
        return questRecord;
    }
}
