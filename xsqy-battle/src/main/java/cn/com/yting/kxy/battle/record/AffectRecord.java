/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.record;

import cn.com.yting.kxy.battle.Actor;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.UnitInitInfo;
import cn.com.yting.kxy.battle.affect.AbstractDamageAffect.DamageType;
import cn.com.yting.kxy.battle.buff.Buff;
import java.util.ArrayList;

import java.util.List;

/**
 *
 * @author Darkholme
 */
public class AffectRecord {

    public enum AffectRecordType {

        DAMAGE,
        RECOVER,
        BUFF_ATTACH,
        BUFF_DETACH,
        DIE,
        FLY_OUT,
        REVIVE,
        SUMMONEE
    }

    public class Value {

        public long hp;
        public long sp;
    }

    public AffectRecordType type;
    public DamageType damageType;
    public Value value = new Value();

    public Actor actor;
    public List<Buff> buffs = new ArrayList<>();
    public Unit target;
    public UnitInitInfo summonee;

    public long sourceId;
    public boolean isHit = true;
    public boolean isCritical;
    public boolean isBlock;
    public boolean isAbsorb;
    public boolean isBless;
    public boolean isOverKill;
    public boolean isMainTarget = true;

    @Override
    public String toString() {
        return "   " + type + " - " + "target " + target.getId() + " - " + "isHit " + isHit;
    }

}
