/*
 * Created 2018-9-11 19:03:18
 */
package cn.com.yting.kxy.web.school;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.PropertyList;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.school.resource.SchoolAbilityInformation;
import cn.com.yting.kxy.web.school.resource.SchoolInformation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "school_record")
@Data
@WebMessageType
public class SchoolRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "school_id", nullable = false)
    private long schoolId;
    @Column(name = "ability_1_level", nullable = false)
    @JsonIgnore
    private int ability_1_level = 1;
    @Column(name = "ability_2_level", nullable = false)
    @JsonIgnore
    private int ability_2_level = 1;
    @Column(name = "ability_3_level", nullable = false)
    @JsonIgnore
    private int ability_3_level = 1;
    @Column(name = "ability_4_level", nullable = false)
    @JsonIgnore
    private int ability_4_level = 1;
    @Column(name = "ability_5_level", nullable = false)
    @JsonIgnore
    private int ability_5_level = 1;
    @Column(name = "ability_6_level", nullable = false)
    @JsonIgnore
    private int ability_6_level = 1;
    @Column(name = "ability_7_level", nullable = false)
    @JsonIgnore
    private int ability_7_level = 1;
    @Column(name = "extra_ability_level_limit", nullable = false)
    private int extra_ability_level_limit = 0;

    @Setter(AccessLevel.NONE)
    private transient List<Integer> ablitiesLevelList;

    public List<Integer> getAblitiesLevelList() {
        if (ablitiesLevelList == null) {
            ablitiesLevelList = PropertyList.<Integer>builder()
                    .add(this::getAbility_1_level, this::setAbility_1_level)
                    .add(this::getAbility_2_level, this::setAbility_2_level)
                    .add(this::getAbility_3_level, this::setAbility_3_level)
                    .add(this::getAbility_4_level, this::setAbility_4_level)
                    .add(this::getAbility_5_level, this::setAbility_5_level)
                    .add(this::getAbility_6_level, this::setAbility_6_level)
                    .add(this::getAbility_7_level, this::setAbility_7_level)
                    .build();
        }
        return ablitiesLevelList;
    }

    public ParameterSpace createParameterSpace(ResourceContext resourceContext) {
        Map<String, ParameterBase> map = new HashMap<>();
        SchoolAbilityInformation[] schoolAbilityInformations = findSchoolAbilityInformations(resourceContext);
        for (int i = 0; i < schoolAbilityInformations.length; i++) {
            final int index = i;
            schoolAbilityInformations[index].getIncrParams().forEach(it -> {
                ParameterBase parameterBase = it.applyToLevel(getAblitiesLevelList().get(index));
                map.merge(it.getName(), parameterBase, ParameterBase::plus);
            });
        }

        double fc = getAblitiesLevelList().stream()
                .mapToDouble(it -> it * 8.2 + 82)
                .sum();
        map.put(ParameterNameConstants.战斗力, new SimpleParameterBase(fc));

        return new SimpleParameterSpace(map);
    }

    public List<Skill> createSkills(ResourceContext resourceContext) {
        List<Skill> skills = new ArrayList<>();
        SchoolAbilityInformation[] schoolAbilityInformations = findSchoolAbilityInformations(resourceContext);

        for (int i = 0; i < schoolAbilityInformations.length; i++) {
            final int index = i;
            schoolAbilityInformations[index].getSkills().forEach(it -> {
                if (getAblitiesLevelList().get(index) >= it.getAbilityLvRequirement()) {
                    skills.add(resourceContext.getLoader(SkillParam.class).get(it.getId()).createSkill(getAblitiesLevelList().get(index)));
                }
            });
        }

        return skills;
    }

    private SchoolAbilityInformation[] findSchoolAbilityInformations(ResourceContext resourceContext) {
        return resourceContext.getLoader(SchoolInformation.class).get(getSchoolId()).getAbilities().stream()
                .map(it -> resourceContext.getLoader(SchoolAbilityInformation.class).get(it.getAbilityId()))
                .toArray(SchoolAbilityInformation[]::new);
    }
}
