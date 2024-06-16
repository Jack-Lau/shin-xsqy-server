/*
 * Created 2016-4-19 14:54:54
 */
package cn.com.yting.kxy.battle.event;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.record.AffectRecord;
import io.github.azige.mgxy.event.EventType;

/**
 *
 * @author Azige
 */
public class DamageEvent extends UnitEvent {

    public enum DamageEventType implements EventType<DamageEvent> {

        AFFECT_DAMAGE
    }

    private final AffectRecord record;

    public DamageEvent(EventType<? extends DamageEvent> type, BattleDirector source, AffectRecord record) {
        super(type, source, record.target);
        this.record = record;
    }

    public DamageEvent(BattleDirector source, AffectRecord record) {
        super(DamageEventType.AFFECT_DAMAGE, source, record.target);
        this.record = record;
    }

    public AffectRecord getRecord() {
        return record;
    }
}