/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.random.pool;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Darkholme
 */
public class PoolSelector {

    private final List<PoolSelectorElement> elements;
    private final RandomSelectType type;

    public PoolSelector(List<PoolSelectorElement> elements, RandomSelectType type) {
        this.elements = new ArrayList<>(elements);
        this.type = type;
    }

    public Collection<PoolSelectorResult> get(Map<Long, PoolValueHolder> poolValueMap) {
        List<PoolSelectorElement> selectElements = new ArrayList<>();
        List<PoolSelectorResult> resultList = new ArrayList<>();
        if (type.equals(RandomSelectType.DEPENDENT)) {
            double weightSum = 0;
            for (PoolSelectorElement element : elements) {
                double weight = getElementActualWeight(poolValueMap, element);
                if (weight > 0) {
                    weightSum += weight;
                    selectElements.add(element);
                }
            }
            double rand = RandomProvider.getRandom().nextDouble() * weightSum;
            PoolSelectorElement element = null;
            for (Iterator<PoolSelectorElement> iterator = selectElements.iterator(); iterator.hasNext() && rand > 0;) {
                element = iterator.next();
                rand -= element.getWeight();
            }
            if (element != null) {
                resultList.add(element.toResult());
                PoolValueHolder valueHolder = poolValueMap.get(element.getId());
                if (valueHolder != null) {
                    valueHolder.decreaseRemainTotalPoolAmount(element.getAmount());
                    valueHolder.decreaseRemainPersonalPoolAmount(element.getAmount());
                }
            }
        } else if (type.equals(RandomSelectType.INDEPENDENT)) {
            Random random = RandomProvider.getRandom();
            for (PoolSelectorElement element : elements) {
                if (random.nextDouble() < getElementActualWeight(poolValueMap, element)) {
                    resultList.add(element.toResult());
                    PoolValueHolder valueHolder = poolValueMap.get(element.getId());
                    if (valueHolder != null) {
                        valueHolder.decreaseRemainTotalPoolAmount(element.getAmount());
                        valueHolder.decreaseRemainPersonalPoolAmount(element.getAmount());
                    }
                };
            }
        }
        return resultList;
    }

    public PoolSelectorResult getSingle(Map<Long, PoolValueHolder> poolValueMap) {
        Collection<PoolSelectorResult> collection = get(poolValueMap);
        if (collection.size() != 1) {
            throw new IllegalStateException("池抽选的结果不是正好1个");
        }
        return collection.iterator().next();
    }

    public RandomSelectType getType() {
        return type;
    }

    public static PoolSelectorBuilder builder() {
        return new PoolSelectorBuilder();
    }

    private double getElementActualWeight(Map<Long, PoolValueHolder> poolValueMap, PoolSelectorElement element) {
        PoolValueHolder poolValue = poolValueMap.get(element.getId());
        if (poolValue != null) {
            if (poolValue.getRemainPersonalPoolAmount() <= 0 || poolValue.getRemainTotalPoolAmount() < element.getAmount()) {
                return 0;
            }
        }
        return element.getWeight();
    }
}
