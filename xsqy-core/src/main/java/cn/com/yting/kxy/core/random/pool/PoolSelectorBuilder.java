/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.random.pool;

import cn.com.yting.kxy.core.random.RandomSelectType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class PoolSelectorBuilder {

    private final List<PoolSelectorElement> elements = new ArrayList<>();

    public PoolSelectorBuilder add(PoolSelectorElement element) {
        elements.add(element);
        return this;
    }

    public PoolSelector build(RandomSelectType type) {
        return new PoolSelector(elements, type);
    }

}
