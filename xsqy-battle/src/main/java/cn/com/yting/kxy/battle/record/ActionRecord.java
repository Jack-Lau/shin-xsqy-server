/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.record;

import cn.com.yting.kxy.battle.buff.Buff;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class ActionRecord {

    public enum ActionRecordType {

        USE_SKILL,
        BUFF_AFFECT,
        BUFF_DECAY,
        SUMMON
    }

    public enum ExecuteResult {

        SUCCESS,
        FAIL_TARGETLOST,
        FAIL_NOTENOUGHCOST
    }

    public class Cost {

        public long sp;
    }

    public ActionRecordType type = ActionRecordType.USE_SKILL;
    public Cost cost = new Cost();
    public ExecuteResult executeResult = ExecuteResult.SUCCESS;
    public int processCount = 1;
    public List<List<AffectRecord>> affectRecordPack = new ArrayList<>();
    public long actionId;
    public long actorId;
    public Buff buffActor;

    @Override
    public String toString() {
        return type + " - " + "action " + actionId + " - " + "actor " + actorId;
    }

}
