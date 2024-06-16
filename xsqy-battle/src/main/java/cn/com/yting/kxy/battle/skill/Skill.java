/*
 * Created 2015-10-12 16:38:49
 */
package cn.com.yting.kxy.battle.skill;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.skill.resource.SkillParam.ElementType;
import cn.com.yting.kxy.battle.skill.resource.SkillParam.SkillType;

/**
 *
 * @author Azige
 */
public interface Skill extends Serializable, JsonSerializable {

    long getId();

    String getName();

    int getLevel();

    void setLevel(int level);

    SkillType getType();

    ElementType getElementType();

    int getTargetType();

    int getMaxTargetCount(Unit source);

    int getMaxProcessCount(Unit source);

    double getExtraMultihitRate(Unit source);

    double getPriority();

    DamageValue getCost();

    SkillParameterTable getSkillParameterTable();

    boolean canManualUse();

    boolean canMultihit();

    boolean checkAvailable(Unit source);

    boolean checkTargetable(Unit source, Unit target);

    void onUnitAttending(BattleDirector battleDirector, Unit unit);

    /**
     * 对备选目标进行排序，以此顺序进行副目标选择
     *
     * @param main
     * @param optionalTargets
     * @return
     */
    List<Unit> selectSecondaryTargets(Unit main, List<Unit> optionalTargets);

    List<Affect> processMainTarget(Unit source, Unit target, int processCount, int targetCount, DamageValue cost);

    List<Affect> processSecondaryTarget(Unit source, Unit target, int processCount, int targetCount, DamageValue cost);

    @Override
    default void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", getId());
        gen.writeObjectField("name", getName());
        gen.writeObjectField("level", getLevel());
        gen.writeEndObject();
    }

    @Override
    default void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
