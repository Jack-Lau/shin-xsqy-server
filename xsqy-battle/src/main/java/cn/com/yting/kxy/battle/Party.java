/*
 * Created 2015-10-8 15:43:04
 */
package cn.com.yting.kxy.battle;

import java.io.Serializable;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Azige
 */
public class Party implements Serializable {

    // 单位位置与其的映射，位置从1到10
    private final SortedMap<Integer, Unit> unitMap;
    private Object formation;

    public Party(Map<Integer, Unit> units) {
        this.unitMap = new TreeMap<>(units);
    }

    public boolean isAnyoneAlive() {
        return !unitMap.values().stream().allMatch(Unit::isHpZero);
    }

    public Map<Integer, Unit> getUnitMap() {
        return unitMap;
    }
}
