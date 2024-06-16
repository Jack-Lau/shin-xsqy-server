/*
 * Created 2016-5-20 18:32:30
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.record.ActionRecord;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Azige
 */
public class DummyAction implements Action {

    @Override
    public ActionType getType() {
        return ActionType.DUMMY;
    }

    @Override
    public List<ActionRecord> getActionRecords() {
        return new ArrayList<>();
    }

}
