/*
 * Created 2015-11-26 14:52:00
 */
package cn.com.yting.kxy.web.award.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class Award {

    private static final Award EMPTY = new Award(0, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    private final int exp;
    private final Map<Long, Long> currencyChanceMap;
    private final List<Long> broadcasts;
    private final List<Long> equipmentPrototypeIds;
    private final List<Long> petPrototypeIds;

    public static Award empty() {
        return EMPTY;
    }

    public Award plus(Award other) {
        List<Long> newBroadcastList = new ArrayList<>(this.broadcasts);
        newBroadcastList.addAll(other.broadcasts);
        List<Long> newEquipmentPrototypeIds = new ArrayList<>(this.equipmentPrototypeIds);
        newEquipmentPrototypeIds.addAll(other.equipmentPrototypeIds);
        List<Long> newPetPrototypeIds = new ArrayList<>(this.petPrototypeIds);
        newPetPrototypeIds.addAll(other.petPrototypeIds);

        Map<Long, Long> newCurrencyChanceMap = new HashMap<>(currencyChanceMap);
        other.currencyChanceMap.keySet().forEach((key) -> {
            long newValue = other.currencyChanceMap.get(key);
            if (newCurrencyChanceMap.containsKey(key)) {
                newValue += newCurrencyChanceMap.get(key);
            }
            newCurrencyChanceMap.put(key, newValue);
        });
        return new Award(
                this.exp + other.exp,
                newCurrencyChanceMap,
                newBroadcastList,
                newEquipmentPrototypeIds,
                newPetPrototypeIds
        );
    }

    /**
     * 将奖励进行规整化。 如果奖励的经验和货币之类的值小于0，则将其设为0。
     *
     * @return
     */
    public Award normalize() {
        boolean changed = false;

        int exp = this.exp;
        if (exp < 0) {
            exp = 0;
            changed = true;
        }
        Map<Long, Long> newCurrencyChanceMap = new HashMap<>(currencyChanceMap);
        for (Entry<Long, Long> entry : newCurrencyChanceMap.entrySet()) {
            if (entry.getValue() < 0) {
                entry.setValue(0L);
                changed = true;
            }
        }

        if (changed) {
            return new Award(exp, newCurrencyChanceMap, broadcasts, equipmentPrototypeIds, petPrototypeIds);
        } else {
            return this;
        }
    }
}
