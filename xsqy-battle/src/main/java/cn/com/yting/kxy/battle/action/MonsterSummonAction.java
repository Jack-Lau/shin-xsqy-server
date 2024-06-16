/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.Party;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.ActionRecord.ActionRecordType;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.record.AffectRecord.AffectRecordType;
import cn.com.yting.kxy.battle.record.Recorder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class MonsterSummonAction implements Action {

    private int position;
    private Unit summonee;
    private final List<ActionRecord> actionRecords = new ArrayList<>();

    @Override
    public ActionType getType() {
        return ActionType.SUMMON;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Unit getSummonee() {
        return summonee;
    }

    public void setSummonee(Unit summonee) {
        this.summonee = summonee;
    }

    public void perform(Unit actor, Recorder recorder, Party actorParty) {
        summonee.setPosition(position);
        actorParty.getUnitMap().put(position, summonee);
        ActionRecord ar = new ActionRecord();
        ar.type = ActionRecordType.SUMMON;
        ar.actorId = actor.getId();
        recorder.addActionRecord(ar);
        this.actionRecords.add(ar);

        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecordType.SUMMONEE;
        affectRecord.target = summonee;
        affectRecord.summonee = summonee.toUnitInitInfo();
        recorder.createAffectRecordPack();
        recorder.addAffectRecord(affectRecord);

        actor.setSummoned(true);
    }

    @Override
    public List<ActionRecord> getActionRecords() {
        return actionRecords;
    }

}
