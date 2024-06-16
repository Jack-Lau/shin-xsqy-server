/*
 * Created 2015-11-4 16:01:42
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.battle.Unit.FashionDye;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.Unit.UnitType;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robots.SkillCollectionRobot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.Skills;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameter;

/**
 *
 * @author Azige
 * @param <UB> 继承此类的类型
 */
public class UnitBuilder<UB extends UnitBuilder<UB>> {

    private long id = -1;
    private long sourceId;
    private long unitDescriptorId;
    private long schoolId;
    //
    private UnitType type;
    private Stance stance;
    private boolean hpVisible = true;
    private boolean flyable = false;
    //
    private String name;
    private long prefabId = 4000001;
    private Long weaponPrefabId;
    private Long titleId;
    private Long fashionId;
    private FashionDye fashionDye;
    private double modelScale = 1.0;
    //
    private Robot robot;
    private final List<Skill> skills = new ArrayList<>();
    private final List<PetUnitBuilder> battlePetList = new ArrayList<>();
    //
    private final List<Parameter> parameters = new ArrayList<>();

    public class PetUnitBuilder extends UnitBuilder<PetUnitBuilder> {

        public UB andUnit() {
            return UnitBuilder.this.chainObject();
        }
    }

    @SuppressWarnings("unchecked")
    public static UnitBuilder<?> create() {
        return new UnitBuilder();
    }

    public UB id(long id) {
        this.id = id;
        return chainObject();
    }

    public UB sourceId(long sourceId) {
        this.sourceId = sourceId;
        return chainObject();
    }

    public UB unitDescriptorId(long unitDescriptorId) {
        this.unitDescriptorId = unitDescriptorId;
        return chainObject();
    }

    public UB schoolId(long schoolId) {
        this.schoolId = schoolId;
        return chainObject();
    }

    public UB type(UnitType type) {
        this.type = type;
        return chainObject();
    }

    public UB stance(Stance stance) {
        this.stance = stance;
        return chainObject();
    }

    public UB hpVisible(boolean hpVisible) {
        this.hpVisible = hpVisible;
        return chainObject();
    }

    public UB flyable() {
        this.flyable = true;
        return chainObject();
    }

    public UB name(String name) {
        this.name = name;
        return chainObject();
    }

    public UB prefabId(long prefabId) {
        this.prefabId = prefabId;
        return chainObject();
    }

    public UB weaponPrefabId(Long weaponPrefabId) {
        this.weaponPrefabId = weaponPrefabId;
        return chainObject();
    }

    public UB titleId(Long titleId) {
        this.titleId = titleId;
        return chainObject();
    }

    public UB fashionId(Long fashionId) {
        this.fashionId = fashionId;
        return chainObject();
    }

    public UB fashionDye(FashionDye fashionDye) {
        this.fashionDye = fashionDye;
        return chainObject();
    }

    public UB modelScale(double modelScale) {
        this.modelScale = modelScale;
        return chainObject();
    }

    public UB attackRobot() {
        this.robot = new SkillCollectionRobot(Collections.emptyList());
        return chainObject();
    }

    public UB robot(Robot robot) {
        this.robot = robot;
        return chainObject();
    }

    public UB attackSkill() {
        skills.add(Skills.ATTACK);
        return chainObject();
    }

    public UB skill(Skill skill) {
        skills.add(Objects.requireNonNull(skill));
        return chainObject();
    }

    public UB skills(Collection<Skill> skills) {
        this.skills.addAll(skills);
        return chainObject();
    }

    public PetUnitBuilder petUnit() {
        PetUnitBuilder pb = new PetUnitBuilder();
        battlePetList.add(pb);
        return pb;
    }

    public UB hp(int value) {
        parameters.add(new SimpleParameter(ParameterNameConstants.最大生命, value));
        return chainObject();
    }

    public UB sp(int value) {
        parameters.add(new SimpleParameter(ParameterNameConstants.最大怒气, value));
        return chainObject();
    }

    public UB parameter(Parameter parameter) {
        parameters.add(parameter);
        return chainObject();
    }

    public UB parameter(String name, double value) {
        parameters.add(new SimpleParameter(name, value));
        return chainObject();
    }

    public UB parameter(Collection<? extends Parameter> parameters) {
        this.parameters.addAll(parameters);
        return chainObject();
    }

    public Unit build() {
        Unit unit = new Unit(name, parameters);
        unit.setId(id);
        unit.setSourceId(sourceId);
        unit.setUnitDescriptorId(unitDescriptorId);
        unit.setSchoolId(schoolId);
        //
        unit.setType(type);
        unit.setStance(stance);
        unit.setHpVisible(hpVisible);
        unit.setFlyable(flyable);
        //
        unit.setName(name);
        unit.setPrefabId(prefabId);
        unit.setWeaponPrefabId(weaponPrefabId);
        unit.setTitleId(titleId);
        unit.setFashionId(fashionId);
        unit.setFashionDye(fashionDye);
        unit.setModelScale(modelScale);
        //
        unit.setRobot(robot);
        unit.getSkills().addAll(skills);
        unit.setBattlePetUnitQueue(
                battlePetList.stream()
                        .map(PetUnitBuilder::build)
                        .collect(Collectors.toCollection(ArrayDeque::new))
        );
        return unit;
    }

    @SuppressWarnings("unchecked")
    protected UB chainObject() {
        return (UB) this;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

}
