/*
 * Created 2015-10-10 17:31:11
 */
package cn.com.yting.kxy.battle.buff;

import cn.com.yting.kxy.battle.Unit;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.core.parameter.ParameterSpace;

/**
 *
 * @author Azige
 */
public class Buff implements Serializable, JsonSerializable {

    private final Unit actor;
    private final long sourceId;
    private final BuffPrototype prototype;
    private final ParameterSpace parameterSpace;
    private int countdown;
    private long effectValue;

    public Buff(Unit actor, long sourceId, BuffPrototype prototype, ParameterSpace parameterSpace, int countdown, long effectValue) {
        this.actor = actor;
        this.sourceId = sourceId;
        this.prototype = prototype;
        this.parameterSpace = parameterSpace;
        this.countdown = countdown;
        this.effectValue = effectValue;
    }

    public long getId() {
        return getPrototype().getId();
    }

    public String getName() {
        return getPrototype().getName();
    }

    public BuffDecayType getDecayType() {
        return getPrototype().getDecayType();
    }

    public BuffMerger getMerger() {
        return getPrototype().getMerger();
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getCountdown() {
        return countdown;
    }

    /**
     * countdown 减少 1
     *
     * @return 减少后剩余的 countdown
     */
    public int decay() {
        return --countdown;
    }

    public BuffPrototype getPrototype() {
        return prototype;
    }

    public List<UnaryOperator<Affect>> getAffectModifiers() {
        return Collections.emptyList();
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", getId());
        gen.writeObjectField("name", getName());
        gen.writeObjectField("countDown", getCountdown());
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the sourceId
     */
    public long getSourceId() {
        return sourceId;
    }

    /**
     * @return the parameterSpace
     */
    public ParameterSpace getParameterSpace() {
        return parameterSpace;
    }

    /**
     * @return the effectValue
     */
    public long getEffectValue() {
        return effectValue;
    }

    /**
     * @return the actor
     */
    public Unit getActor() {
        return actor;
    }

}
