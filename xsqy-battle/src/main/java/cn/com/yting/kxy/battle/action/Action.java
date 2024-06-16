/*
 * Created 2015-10-26 19:28:46
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.record.ActionRecord;
import java.util.List;

/**
 *
 * @author Azige
 */
public interface Action {

    ActionType getType();

    List<ActionRecord> getActionRecords();

}
