/*
 * Created 2016-1-8 15:41:09
 */
package cn.com.yting.kxy.battle.generate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robots.SkillCollectionRobot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.battle.skill.resource.SkillParamLoader;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;

/**
 *
 * @author Azige
 */
public class UnitDescriptor implements Resource {

    private static final String MONSTER_LEVEL_VARIABLE_NAME = "MLV";

    private long id;
    private String name;
    private long titleId;
    private long prefabId;
    private int weaponSerialId;
    private double modelScale;
    private String levelExpr;
    private List<Long> skillIds;
    private Map<String, String> parameterExprMap;
    private Long robotId;
    private boolean flyable;

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

    public long getTitleId() {
        return titleId;
    }

    public void setTitleId(long titleId) {
        this.titleId = titleId;
    }

    public long getPrefabId() {
        return prefabId;
    }

    public void setPrefabId(long prefabId) {
        this.prefabId = prefabId;
    }

    public String getLevelExpr() {
        return levelExpr;
    }

    public void setLevelExpr(String levelExpr) {
        this.levelExpr = levelExpr;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    public Map<String, String> getParameterExprMap() {
        return parameterExprMap;
    }

    public void setParameterExprMap(Map<String, String> parameterExprMap) {
        this.parameterExprMap = parameterExprMap;
    }

    public Long getRobotId() {
        return robotId;
    }

    public void setRobotId(Long robotId) {
        this.robotId = robotId;
    }

    public void export(UnitBuilder<?> ub, ResourceContext resourceContext) {
        int monsterLevel = Integer.parseInt(levelExpr);
        SkillParamLoader skillParamLoader = (SkillParamLoader) (resourceContext.getLoader(SkillParam.class));
        List<Skill> skills = skillIds.stream()
                .map(id -> {
                    return skillParamLoader.get(id).createSkill(monsterLevel);
                })
                .collect(Collectors.toList());

        ub
                .id(id)
                .name(name)
                .titleId(titleId)
                .prefabId(prefabId)
                .weaponPrefabId(weaponSerialId == 0 ? null : (long) weaponSerialId)
                .modelScale(modelScale)
                .attackSkill()
                .skills(skills)
                .unitDescriptorId(id);

        if (flyable) {
            ub.flyable();
        }
        ResourceLoader<Robot> robotLoader = resourceContext.getLoader(Robot.class);
        if (robotId != null) {
            ub.robot(robotLoader.get(robotId));
        } else {
            ub.robot(new SkillCollectionRobot(skills));
        }

        for (Entry<String, String> entrySet : parameterExprMap.entrySet()) {
            String key = entrySet.getKey();
            if (key.equals(ParameterNameConstants.等级)) {
                continue;
            }
            String value = entrySet.getValue();
            ub.parameter(key, Double.parseDouble(value));
        }

        ub.parameter(ParameterNameConstants.等级, monsterLevel);
    }

    /**
     * @return the flyable
     */
    public boolean isFlyable() {
        return flyable;
    }

    /**
     * @param flyable the flyable to set
     */
    public void setFlyable(boolean flyable) {
        this.flyable = flyable;
    }

    /**
     * @return the weaponSerialId
     */
    public int getWeaponSerialId() {
        return weaponSerialId;
    }

    /**
     * @param weaponSerialId the weaponSerialId to set
     */
    public void setWeaponSerialId(int weaponSerialId) {
        this.weaponSerialId = weaponSerialId;
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
}
