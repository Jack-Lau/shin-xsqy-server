/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.BattleConstant;
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
public class PlayerSummonAction extends SummonAction {

    private final List<ActionRecord> actionRecords = new ArrayList<>();

    @Override
    public void perform(Unit actor, Recorder recorder, Party actorParty) {
        if (!actor.getBattlePetUnitQueue().isEmpty()) {
            Unit pet = actor.getBattlePetUnitQueue().poll();
            int position = actor.getPosition() + BattleConstant.PET_POSITION_OFFSET;
            pet.setPosition(position);
            actorParty.getUnitMap().put(position, pet);

            ActionRecord ar = new ActionRecord();
            ar.type = ActionRecordType.SUMMON;
            ar.actorId = actor.getId();
            recorder.addActionRecord(ar);
            this.actionRecords.add(ar);

            AffectRecord affectRecord = new AffectRecord();
            affectRecord.type = AffectRecordType.SUMMONEE;
            affectRecord.target = pet;
            affectRecord.summonee = pet.toUnitInitInfo();
            recorder.createAffectRecordPack();
            recorder.addAffectRecord(affectRecord);

            actor.setSummoned(true);
        }
    }

    @Override
    public List<ActionRecord> getActionRecords() {
        return actionRecords;
    }

}
