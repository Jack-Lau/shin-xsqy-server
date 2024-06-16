/*
 * Created 2018-8-3 16:14:23
 */
package cn.com.yting.kxy.web.award.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Azige
 */
public class AwardBuilder {

    private int exp;
    private final Map<Long, Long> currencyChanceMap = new HashMap<>();
    private final List<Long> broadcasts = new ArrayList<>();
    private final List<Long> equipmentPrototypeIds = new ArrayList<>();
    private final List<Long> petPrototypeIds = new ArrayList<>();

    public void addExp(int value) {
        exp += value;
    }

    public void addCurrencyChance(long currencyId, long amount) {
        currencyChanceMap.merge(currencyId, amount, Long::sum);
    }

    public void addBroadcast(long broadcastId) {
        broadcasts.add(broadcastId);
    }

    public void addEquipmentPrototypeId(long equipmentPrototypeId) {
        equipmentPrototypeIds.add(equipmentPrototypeId);
    }

    public void addPetPrototypeId(long petPrototypeId) {
        petPrototypeIds.add(petPrototypeId);
    }

    public Award build() {
        return new Award(exp, currencyChanceMap, broadcasts, equipmentPrototypeIds, petPrototypeIds).normalize();
    }
}
