/*
 * Created 2018-8-4 15:59:46
 */
package cn.com.yting.kxy.web.quest;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class QuestException extends KxyWebException {

    public static final int EC_ILLEGAL_STATUS_FOR_START = 700;
    public static final int EC_REACH_START_LIMIT = 701;
    public static final int EC_NOT_MEET_PREREQUIREMENT = 702;
    public static final int EC_OBJECTIVE_ALREADY_DONE = 703;
    public static final int EC_ILLEGAL_STATUS_FOR_ACHIEVE_OBJ = 704;
    public static final int EC_OBJECTIVE_NOT_READY_TO_DONE = 705;
    public static final int EC_INSUFFICIENT_CURRENCY = 706;

    public QuestException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static QuestException illegalStatusForStart() {
        return new QuestException(EC_ILLEGAL_STATUS_FOR_START, "要开始的任务状态不对");
    }

    public static QuestException reachStartLimit() {
        return new QuestException(EC_REACH_START_LIMIT, "已达到领取次数上限");
    }

    public static QuestException notMeetPrerequirement() {
        return new QuestException(EC_NOT_MEET_PREREQUIREMENT, "未达到前置领取条件");
    }

    public static QuestException objectiveAlreadyDone() {
        return new QuestException(EC_OBJECTIVE_ALREADY_DONE, "任务目标已完成");
    }

    public static QuestException illegalStatusForAchieveObjective() {
        return new QuestException(EC_ILLEGAL_STATUS_FOR_START, "要完成目标的任务状态不对");
    }

    public static QuestException objectiveNotReadyToDone() {
        return new QuestException(EC_OBJECTIVE_NOT_READY_TO_DONE, "尚未达到任务目标");
    }

    public static QuestException insufficientCurrency() {
        return new QuestException(EC_INSUFFICIENT_CURRENCY, "拥有的货币不足");
    }

}
