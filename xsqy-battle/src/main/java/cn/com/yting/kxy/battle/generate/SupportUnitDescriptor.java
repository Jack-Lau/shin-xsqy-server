/*
 * Created 2016-3-2 17:02:11
 */
package cn.com.yting.kxy.battle.generate;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.Unit.Stance;

import java.util.Collection;
import java.util.List;

import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.parameter.Parameter;

/**
 *
 * @author Azige
 */
public class SupportUnitDescriptor {

    private long id;
    private long sourceId;
    private long schoolId;
    //
    private String name;
    private long prefabId;
    private long weaponPrefabId;
    private long titleId;
    //
    private int aiId = 1;
    private Collection<Skill> skills;
    private List<SupportUnitDescriptor> pets;
    private Collection<Parameter> parameters;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrefabId() {
        return prefabId;
    }

    public void setPrefabId(long prefabId) {
        this.prefabId = prefabId;
    }

    public Collection<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }

    public Collection<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Collection<Skill> skills) {
        this.skills = skills;
    }

    public List<SupportUnitDescriptor> getPets() {
        return pets;
    }

    public void setPets(List<SupportUnitDescriptor> pets) {
        this.pets = pets;
    }

    /**
     * @return the aiId
     */
    public int getAiId() {
        return aiId;
    }

    /**
     * @param aiId the aiId to set
     */
    public void setAiId(int aiId) {
        this.aiId = aiId;
    }

    public void export(UnitBuilder<?> ub, Stance stance) {
        ub
                .id(getId())
                .sourceId(sourceId)
                .schoolId(schoolId)
                //
                .type(Unit.UnitType.TYPE_PLAYER)
                .stance(stance)
                //
                .name(getName())
                .prefabId(getPrefabId())
                .weaponPrefabId(getWeaponPrefabId())
                .titleId(titleId)
                //
                .skills(getSkills())
                .parameter(getParameters());
        if (getPets() != null) {
            getPets().forEach(pet -> {
                ub
                        .petUnit()
                        .id(pet.getId())
                        .sourceId(pet.sourceId)
                        //
                        .type(Unit.UnitType.TYPE_PET)
                        .stance(stance)
                        .flyable()
                        //
                        .name(pet.getName())
                        .prefabId(pet.getPrefabId())
                        //
                        .skills(pet.getSkills())
                        .parameter(pet.getParameters());
            });
        }
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
     * @return the schoolId
     */
    public long getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @param weaponPrefabId the weaponPrefabId to set
     */
    public void setWeaponPrefabId(long weaponPrefabId) {
        this.weaponPrefabId = weaponPrefabId;
    }

    /**
     * @return the titleId
     */
    public long getTitleId() {
        return titleId;
    }

    /**
     * @param titleId the titleId to set
     */
    public void setTitleId(long titleId) {
        this.titleId = titleId;
    }

    /**
     * @return the weaponPrefabId
     */
    public long getWeaponPrefabId() {
        return weaponPrefabId;
    }

}
