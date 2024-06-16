/*
 * Created 2015-10-26 18:17:40
 */
package cn.com.yting.kxy.battle.buff;

import java.io.Serializable;

import cn.com.yting.kxy.battle.Actor;
import cn.com.yting.kxy.battle.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.yting.kxy.core.parameter.ParameterSpace;

/**
 * Buff 原型类，用于提供特定的 Buff 对象
 *
 * @author Azige
 */
public class BuffPrototype implements Serializable, Actor {

    private static final Logger logger = LoggerFactory.getLogger(BuffPrototype.class);

    private final long id;
    private final String name;
    private final Type type;
    private final BuffDecayType decayType;
    private final BuffMerger merger;

    public enum Type {

        默认, 增益, 减益, 控制
    }

    public BuffPrototype(
            long id,
            String name,
            Type type,
            BuffDecayType decayType,
            BuffMerger merger
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.decayType = decayType;
        this.merger = merger;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public BuffDecayType getDecayType() {
        return decayType;
    }

    public BuffMerger getMerger() {
        return merger;
    }

    public Buff createBuff(Unit actor, long sourceId, ParameterSpace parameterSpace, int countdown, long effectValue) {
        return new Buff(actor, sourceId, this, parameterSpace, countdown, effectValue);
    }

    @Override
    public String toString() {
        return "BuffPrototype{" + "id=" + id + ", name=" + name + ", type=" + type + ", decayType=" + decayType + ", merger=" + merger + '}';
    }

}
