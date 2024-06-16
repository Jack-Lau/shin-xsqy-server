/*
 * Created 2017-2-20 11:34:54
 */
package cn.com.yting.kxy.battle.executor.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class UnitModel {

    String name;
    int level;
    Map<String, Double> parameterMap;
    List<Long> skillIds;
    Long robotId;

    public UnitModel() {
    }

    public UnitModel(String name, int level, Map<String, Double> parameterMap, List<Long> skillIds, Long robotId) {
        this.name = name;
        this.level = level;
        this.parameterMap = parameterMap;
        this.skillIds = skillIds;
        this.robotId = robotId;
    }
}
