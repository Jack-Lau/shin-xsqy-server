/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;

/**
 *
 * @author Darkholme
 */
public final class SimpleAffects {

    public static void bless(BattleDirector bd, Unit target, DamageValue recoverValue, long sourceId) {
        target.takeRecover(recoverValue);
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.RECOVER;
        affectRecord.value.hp = (long) recoverValue.getHp();
        affectRecord.target = target;
        affectRecord.sourceId = sourceId;
        affectRecord.isBless = true;
        bd.addAffectRecord(affectRecord);
    }

    public static void recover(BattleDirector bd, Unit target, DamageValue recoverValue, long sourceId) {
        target.takeRecover(recoverValue);
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.RECOVER;
        affectRecord.value.hp = (long) recoverValue.getHp();
        affectRecord.value.sp = (long) recoverValue.getSp();
        affectRecord.actor = target;
        affectRecord.target = target;
        affectRecord.sourceId = sourceId;
        bd.addAffectRecord(affectRecord);
    }

    public static void attachBuff(BattleDirector bd, Unit actor, Unit target, long buffPrototypeId, int countDown, ParameterSpace ps, long sourceId) {
        BuffPrototype prototype = ResourceContextHolder.getResourceContext().createReference(BuffParam.class, buffPrototypeId).get().getPrototype();
        Buff buff = prototype.createBuff(actor, sourceId, ps, countDown, 1);
        target.addBuff(buff);
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.BUFF_ATTACH;
        affectRecord.target = target;
        affectRecord.sourceId = buff.getSourceId();
        affectRecord.buffs.add(buff);
        affectRecord.isHit = true;
        bd.addAffectRecord(affectRecord);
    }

    public static void damage(BattleDirector bd, Unit actor, Unit target, DamageValue damageValue, long sourceId) {
        target.takeDamage(damageValue);
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.DAMAGE;
        affectRecord.damageType = AbstractDamageAffect.DamageType.ABSOLUTE;
        affectRecord.value.hp = (long) damageValue.getHp();
        affectRecord.value.sp = (long) damageValue.getSp();
        affectRecord.actor = actor;
        affectRecord.target = target;
        affectRecord.sourceId = sourceId;
        bd.addAffectRecord(affectRecord);
    }

}
