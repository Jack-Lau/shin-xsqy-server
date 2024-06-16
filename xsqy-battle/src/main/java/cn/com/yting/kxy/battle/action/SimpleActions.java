/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect;
import cn.com.yting.kxy.battle.affect.AttachBuffAffect;
import cn.com.yting.kxy.battle.affect.DetachBuffAffect;
import cn.com.yting.kxy.battle.affect.RecoverAffect;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;
import java.util.stream.Collectors;

/**
 *
 * @author Darkholme
 */
public final class SimpleActions {

    public static void useSkill(BattleDirector bd, Unit actor, Unit target, Skill skill) {
        UseSkillAction action = new UseSkillAction(skill, target);
        action.perform(actor, bd, bd.getAllUnits().collect(Collectors.toList()));
    }

    public static void buffAffect_回春(BattleDirector bd, Unit actor, SkillParameterTable spt, int skillLevel, long sourceId) {
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_AFFECT;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = sourceId;
        bd.addActionRecord(actionRecord);
        bd.createAffectRecordPack();
        bd.addAffectRecord(new RecoverAffect(false, actor, spt, skillLevel, 1, 1).affect(actor));
    }

    public static void buffAffect_再生(BattleDirector bd, Unit actor, DamageValue recoverValue, long sourceId) {
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_AFFECT;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = sourceId;
        bd.addActionRecord(actionRecord);
        actor.takeRecover(recoverValue);
        bd.createAffectRecordPack();
        AffectRecord affectRecord = new AffectRecord();
        affectRecord.type = AffectRecord.AffectRecordType.RECOVER;
        affectRecord.value.hp = (long) recoverValue.getHp();
        affectRecord.value.sp = (long) recoverValue.getSp();
        affectRecord.actor = actor;
        affectRecord.target = actor;
        affectRecord.sourceId = sourceId;
        bd.addAffectRecord(affectRecord);
    }

    public static void buffAffect_流血(BattleDirector bd, Unit actor, Unit target, long sourceId) {
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_AFFECT;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = sourceId;
        bd.addActionRecord(actionRecord);
        bd.createAffectRecordPack();
        int processCount = 1;
        //
        if (actor.getParameter(ParameterNameConstants.盘丝奇穴5_乙).getValue() > 0
                && RandomProvider.getRandom().nextDouble() < 0.15 + actor.getParameter(ParameterNameConstants.盘丝奇穴5_乙).getValue() * 0.0425) {
            if (RandomProvider.getRandom().nextDouble() < (actor.getParameter(ParameterNameConstants.连击率).getValue() + 0.05)) {
                processCount++;
            }
        }
        for (int i = 0; i < processCount; i++) {
            double damageValue = target.getParameter(ParameterNameConstants.流血值).getValue();
            boolean criticalFlag = false;
            if (actor.getParameter(ParameterNameConstants.盘丝奇穴4_乙).getValue() > 0
                    && RandomProvider.getRandom().nextDouble() < 0.16 + actor.getParameter(ParameterNameConstants.盘丝奇穴4_乙).getValue() * 0.042) {
                if (RandomProvider.getRandom().nextDouble() < (actor.getParameter(ParameterNameConstants.暴击率).getValue() + 0.05)) {
                    criticalFlag = true;
                    damageValue *= (1 + (actor.getParameter(ParameterNameConstants.暴击效果).getValue() - 1));
                }
            }
            //
            if (target.getHp().getValue() <= damageValue) {
                damageValue = target.getHp().getValue() - 1;
            }
            target.getHp().shift(-damageValue);
            AffectRecord affectRecord = new AffectRecord();
            affectRecord.type = AffectRecord.AffectRecordType.DAMAGE;
            affectRecord.damageType = AbstractDamageAffect.DamageType.PHYSICS;
            affectRecord.value.hp = (long) damageValue;
            affectRecord.actor = actor;
            affectRecord.target = target;
            affectRecord.isCritical = criticalFlag;
            affectRecord.sourceId = sourceId;
            bd.addAffectRecord(affectRecord);
        }
    }

    public static void buffAttach(BattleDirector bd, Unit actor, Unit target, long buffPrototypeId, int countDown, ParameterSpace ps, long sourceId) {
        BuffPrototype prototype = ResourceContextHolder.getResourceContext().createReference(BuffParam.class, buffPrototypeId).get().getPrototype();
        Buff buff = prototype.createBuff(actor, sourceId, ps, countDown, 1);
        //
        SkillParameterTable sp = SkillParameterTable.builder()
                .固定命中率(1)
                .build();
        //
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_AFFECT;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = sourceId;
        bd.addActionRecord(actionRecord);
        bd.createAffectRecordPack();
        bd.addAffectRecord(new AttachBuffAffect(target, buff, sp, 1, 1).affect(target));
    }

    public static void buffDetach(BattleDirector bd, Unit actor, Unit target, long sourceId) {
        SkillParameterTable sp = SkillParameterTable.builder()
                .固定命中率(1)
                .build();
        //
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_AFFECT;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = sourceId;
        bd.addActionRecord(actionRecord);
        bd.createAffectRecordPack();
        bd.addAffectRecord(new DetachBuffAffect(actor, "万蛊噬心", sp, 1, 1).affect(target));
        bd.addAffectRecord(new DetachBuffAffect(actor, "封魂咒", sp, 1, 1).affect(target));
    }

    public static void buffDecay(BattleDirector bd, Buff buffActor, Unit target) {
        int countdown = buffActor.decay();
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.type = ActionRecord.ActionRecordType.BUFF_DECAY;
        actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;
        actionRecord.actorId = target.getId();
        actionRecord.buffActor = buffActor;
        bd.addActionRecord(actionRecord);
        if (countdown <= 0) {
            target.removeBuff(buffActor.getName());
            AffectRecord affectRecord = new AffectRecord();
            affectRecord.type = AffectRecord.AffectRecordType.BUFF_DETACH;
            affectRecord.target = target;
            affectRecord.buffs.add(buffActor);
            bd.createAffectRecordPack();
            bd.addAffectRecord(affectRecord);
        }
    }

}
