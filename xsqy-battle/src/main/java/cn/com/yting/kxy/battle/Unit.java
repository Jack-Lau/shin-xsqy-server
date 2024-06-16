/*
 * Created 2015-10-8 15:48:41
 */
package cn.com.yting.kxy.battle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import cn.com.yting.kxy.battle.buff.Buff;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.RangedParameter;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class Unit implements Serializable, JsonSerializable, Actor {

    private static Logger logger = LoggerFactory.getLogger(Unit.class);

    public enum UnitType {

        TYPE_PLAYER,
        TYPE_PET,
        TYPE_MONSTER
    }

    public enum Stance {

        STANCE_RED,
        STANCE_BLUE
    }

    private long id;
    private long sourceId;
    private long unitDescriptorId;
    private long schoolId;
    //
    private UnitType type;
    private Stance stance;
    private int position;
    private boolean hpVisible = true;
    private boolean flyable;
    //
    private String name;
    private long prefabId;
    private Long weaponPrefabId;
    private Long titleId;
    private Long fashionId;
    private FashionDye fashionDye;
    private double modelScale = 1.0;
    //
    private Robot robot;
    private List<Skill> skills = new ArrayList<>();
    private Queue<Unit> battlePetUnitQueue = new ArrayDeque<>();
    //
    private RangedParameter hp;
    private RangedParameter sp;
    private ParameterSpace baseParameterSpace;
    private ParameterSpace extraParameterSpace;
    private RootParameterSpace rootParameterSpace;
    //
    private Map<String, Buff> buffMap = new LinkedHashMap<>();
    private boolean dead = false;
    private boolean flyOut = false;
    private boolean summoned = false;
    //单位的怒火补正率，用于伤害计算
    private double 怒火补正率 = 0.0;

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", getId());
        gen.writeObjectField("name", getName());
        gen.writeEndObject();
    }

    @Value
    public static class FashionDye {

        private int part_1_color;
        private int part_1_saturation;
        private int part_1_brightness;

        private int part_2_color;
        private int part_2_saturation;
        private int part_2_brightness;

        private int part_3_color;
        private int part_3_saturation;
        private int part_3_brightness;
    }

    public UnitInitInfo toUnitInitInfo() {
        UnitInitInfo unitInitInfo = new UnitInitInfo();
        unitInitInfo.id = getId();
        unitInitInfo.sourceId = getSourceId();
        unitInitInfo.unitDescriptorId = getUnitDescriptorId();
        unitInitInfo.schoolId = getSchoolId();
        //
        unitInitInfo.type = getType();
        unitInitInfo.stance = getStance();
        unitInitInfo.position = getPosition();
        unitInitInfo.hpVisible = isHpVisible();
        //
        unitInitInfo.name = getName();
        unitInitInfo.prefabId = getPrefabId();
        unitInitInfo.weaponPrefabId = getWeaponPrefabId();
        unitInitInfo.titleId = getTitleId();
        unitInitInfo.fashionId = getFashionId();
        unitInitInfo.fashionDye = getFashionDye();
        unitInitInfo.modelScale = getModelScale();
        //
        unitInitInfo.maxHp = (long) getHp().getUpperLimit().getValue();
        unitInitInfo.maxSp = (long) getSp().getUpperLimit().getValue();
        unitInitInfo.hp = (long) getHp().getValue();
        unitInitInfo.sp = (long) getSp().getValue();
        unitInitInfo.怒气消耗率 = getParameter(ParameterNameConstants.怒气消耗率).getValue();
        //
        getSkills().forEach((s) -> {
            unitInitInfo.skillIds.add(s.getId());
        });
        return unitInitInfo;
    }

    public UnitStatus toUnitStatus() {
        UnitStatus unitStatus = new UnitStatus();
        unitStatus.id = getId();
        unitStatus.currHp = (long) getHp().getValue();
        unitStatus.currSp = (long) getSp().getValue();
        unitStatus.怒火补正率 = get怒火补正率();
        unitStatus.怒气消耗率 = getParameter(ParameterNameConstants.怒气消耗率).getValue();
        return unitStatus;
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        //
    }

    public Unit(String name, Collection<Parameter> parameters) {
        this.name = name;
        Map<String, Parameter> parameterMap = parameters.stream()
                .collect(Collectors.toMap(Parameter::getName, Function.identity()));
        baseParameterSpace = new SimpleParameterSpace(
                parameters.stream()
                        .collect(Collectors.toMap(Parameter::getName, p -> new SimpleParameterBase(p.getValue())))
        );
        extraParameterSpace = new SimpleParameterSpace();
        this.hp = Optional.ofNullable(parameterMap.get(ParameterNameConstants.最大生命))
                .map(p -> new RangedParameter(ParameterNameConstants.最大生命, p.getValue()))
                .orElseThrow(() -> {
                    return new IllegalArgumentException("缺少生命参数");
                });
        if (parameterMap.get(ParameterNameConstants.最大怒气) != null
                && parameterMap.get(ParameterNameConstants.初始怒气) != null
                && parameterMap.get(ParameterNameConstants.额外初始怒气) != null) {
            this.sp = new RangedParameter(ParameterNameConstants.最大怒气,
                    parameterMap.get(ParameterNameConstants.初始怒气).getValue() + parameterMap.get(ParameterNameConstants.额外初始怒气).getValue(),
                    0,
                    parameterMap.get(ParameterNameConstants.最大怒气).getValue());
        } else {
            this.sp = new RangedParameter(ParameterNameConstants.最大怒气, 50, 0, 100);
        }
        rebuildRootSpace();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
        this.type = type;
    }

    public Stance getStance() {
        return stance;
    }

    public void setStance(Stance stance) {
        this.stance = stance;
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public boolean isHpVisible() {
        return hpVisible;
    }

    public void setHpVisible(boolean hpVisible) {
        this.hpVisible = hpVisible;
    }

    public double getSpeed() {
        return getRootParameterSpace().getParameterBase(ParameterNameConstants.速度).exportValue();
    }

    public RangedParameter getHp() {
        return hp;
    }

    public boolean costHp(int value) {
        return cost(value, getHp());
    }

    public RangedParameter getSp() {
        return sp;
    }

    public int getSpCost(double skillSpCost) {
        return (int) (getParameter(ParameterNameConstants.怒气消耗率).getValue() * skillSpCost);
    }

    public boolean costSp(int value) {
        return cost(value, getSp());
    }

    private boolean cost(int value, RangedParameter para) {
        if (para.getValue() >= value) {
            para.shift(-value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得指定的单位的战斗参数，如果属性未设置，则返回一个值为0的简单参数
     *
     * @param name 参数名
     * @return 指定的参数，总是不为 null
     */
    public Parameter getParameter(String name) {
        return getRootParameterSpace().getParameter(name);
    }

    public RootParameterSpace getRootParameterSpace() {
        return rootParameterSpace;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public boolean isPet() {
        return UnitType.TYPE_PET.equals(getType());
    }

    public boolean isFlyable() {
        return flyable;
    }

    public void setFlyable(boolean flyable) {
        this.flyable = flyable;
    }

    public boolean isHpZero() {
        return getHp().getValue() <= 0;
    }

    public boolean getIsDead() {
        return isDead();
    }

    /**
     * @param dead the isHpZero to set
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * @return the flyOut
     */
    public boolean isFlyOut() {
        return flyOut;
    }

    /**
     * @param flyOut the flyOut to set
     */
    public void setFlyOut(boolean flyOut) {
        this.flyOut = flyOut;
    }

    public Queue<Unit> getBattlePetUnitQueue() {
        return battlePetUnitQueue;
    }

    public void setBattlePetUnitQueue(Queue<Unit> battlePetUnitQueue) {
        this.battlePetUnitQueue = battlePetUnitQueue;
    }

    /**
     * 给此单位加上一个 buff。
     *
     * @param buff
     * @return
     */
    public boolean addBuff(Buff buff) {
        Objects.requireNonNull(buff);
        Buff oldBuff = getBuffMap().get(buff.getName());
//        if (oldBuff != null) {
//            boolean merged = oldBuff.merge(buff);
//            return merged;
//        } else {
//            buffMap.put(buff.getName(), buff);
//            buff.onAttach(this);
//            return true;
//        }
        if (oldBuff != null) {
            buff = buff.getMerger().merge(oldBuff, buff);
            if (buff == oldBuff) {
                return false;
            }
        }
        getBuffMap().put(buff.getName(), buff);
        rebuildRootSpace();
        return true;
    }

    public boolean hasBuff(String name) {
        return getBuffMap().containsKey(name);
    }

    public Buff getBuff(String name) {
        return getBuffMap().get(name);
    }

    public Collection<Buff> getBuffs() {
        return getBuffMap().values();
    }

    /**
     * 按名字移除一个 Buff
     *
     * @param name
     * @return 如果移除成功为对应的 Buff 对象，否则为 null
     */
    public Buff removeBuff(String name) {
        Buff buff = getBuffMap().remove(name);
        if (buff != null) {
            rebuildRootSpace();
            return buff;
        } else {
            return null;
        }
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getPrefabId() {
        return prefabId;
    }

    public void setPrefabId(long prefabId) {
        this.prefabId = prefabId;
    }

    public void destroy() {
    }

    public boolean isTargetable(Unit actor) {
        return !isFlyOut();
    }

    public void takeDamage(DamageValue value) {
        getHp().shift(-value.getHp());
        getSp().shift(-value.getSp());
    }

    public void takeRecover(DamageValue value) {
        getHp().shift(value.getHp());
        getSp().shift(value.getSp());
    }

    public boolean isAlly(Unit another) {
        return getStance().equals(another.getStance());
    }

    public boolean cost(DamageValue costValue) {
        if (checkCost(costValue)) {
            getHp().shift(-costValue.getHp());
            getSp().shift(-costValue.getSp());
            return true;
        } else {
            return false;
        }
    }

    public boolean checkCost(DamageValue costValue) {
        return getHp().getValue() > costValue.getHp()
                && getSp().getValue() >= costValue.getSp();
    }

    public boolean hasSkillPrototype(long prototypeId) {
        if (getSkills().stream().anyMatch((s) -> (s.getId() == prototypeId))) {
            return true;
        }
        return false;
    }

    public Skill getSkill(long prototypeId) {
        for (Skill s : getSkills()) {
            if (s.getId() == prototypeId) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "--Battle Unit {"
                + getId() + " "
                + getName() + " "
                + getPrefabId() + " "
                + getHp().getValue() + " "
                + getSp().getValue() + " "
                + "}";
    }

    private ParameterSpace getBuffSpace() {
        List<ParameterSpace> parameterSpaces = new ArrayList<>();
        getBuffs().forEach((buff) -> {
            parameterSpaces.add(buff.getParameterSpace());
        });
        return new AggregateParameterSpace(parameterSpaces);
    }

    private void rebuildRootSpace() {
        setRootParameterSpace(new AggregateParameterSpace(getBaseParameterSpace(), getExtraParameterSpace(), getBuffSpace()).asRootParameterSpace());
    }

    /**
     * @return the weaponPrefabId
     */
    public Long getWeaponPrefabId() {
        return weaponPrefabId;
    }

    /**
     * @param weaponPrefabId the weaponPrefabId to set
     */
    public void setWeaponPrefabId(Long weaponPrefabId) {
        this.weaponPrefabId = weaponPrefabId;
    }

    /**
     * @return the modelScale
     */
    public double getModelScale() {
        return modelScale;
    }

    /**
     * @param modelScale the modelScale to set
     */
    public void setModelScale(double modelScale) {
        this.modelScale = modelScale;
    }

    public long getUnitDescriptorId() {
        return unitDescriptorId;
    }

    public void setUnitDescriptorId(long unitDescriptorId) {
        this.unitDescriptorId = unitDescriptorId;
    }

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    /**
     * @return the extraParameterSpace
     */
    public ParameterSpace getExtraParameterSpace() {
        return extraParameterSpace;
    }

    /**
     * @param extraParameterSpace the extraParameterSpace to set
     */
    public void setExtraParameterSpace(ParameterSpace extraParameterSpace) {
        this.extraParameterSpace = extraParameterSpace;
    }

    /**
     * @return the 怒火补正率
     */
    public double get怒火补正率() {
        return 怒火补正率;
    }

    /**
     * @param 怒火补正率 the 怒火补正率 to set
     */
    public void set怒火补正率(double 怒火补正率) {
        this.怒火补正率 = 怒火补正率;
    }

    /**
     * @return the summoned
     */
    public boolean isSummoned() {
        return summoned;
    }

    /**
     * @param summoned the summoned to set
     */
    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    /**
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * @param aLogger the logger to set
     */
    public static void setLogger(Logger aLogger) {
        logger = aLogger;
    }

    /**
     * @return the sourceId
     */
    public long getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId the sourceId to set
     */
    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param skills the skills to set
     */
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(RangedParameter hp) {
        this.hp = hp;
    }

    /**
     * @param sp the sp to set
     */
    public void setSp(RangedParameter sp) {
        this.sp = sp;
    }

    /**
     * @return the baseParameterSpace
     */
    public ParameterSpace getBaseParameterSpace() {
        return baseParameterSpace;
    }

    /**
     * @param baseParameterSpace the baseParameterSpace to set
     */
    public void setBaseParameterSpace(ParameterSpace baseParameterSpace) {
        this.baseParameterSpace = baseParameterSpace;
    }

    /**
     * @param rootParameterSpace the rootParameterSpace to set
     */
    public void setRootParameterSpace(RootParameterSpace rootParameterSpace) {
        this.rootParameterSpace = rootParameterSpace;
    }

    /**
     * @return the buffMap
     */
    public Map<String, Buff> getBuffMap() {
        return buffMap;
    }

    /**
     * @param buffMap the buffMap to set
     */
    public void setBuffMap(Map<String, Buff> buffMap) {
        this.buffMap = buffMap;
    }

    /**
     * @return the isHpZero
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @return the fashionId
     */
    public Long getFashionId() {
        return fashionId;
    }

    /**
     * @param fashionId the fashionId to set
     */
    public void setFashionId(Long fashionId) {
        this.fashionId = fashionId;
    }

    /**
     * @return the fashionDye
     */
    public FashionDye getFashionDye() {
        return fashionDye;
    }

    /**
     * @param fashionDye the fashionDye to set
     */
    public void setFashionDye(FashionDye fashionDye) {
        this.fashionDye = fashionDye;
    }

}
