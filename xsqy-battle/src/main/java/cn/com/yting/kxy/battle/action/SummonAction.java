/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.Party;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.record.Recorder;

/**
 *
 * @author Darkholme
 */
public abstract class SummonAction implements Action {

    @Override
    public ActionType getType() {
        return ActionType.SUMMON;
    }

    public abstract void perform(Unit actor, Recorder recorder, Party actorParty);

}
