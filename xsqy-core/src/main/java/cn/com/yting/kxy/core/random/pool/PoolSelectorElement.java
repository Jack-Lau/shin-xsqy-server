/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.random.pool;

import jakarta.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 直接映射到配置表中的类型，所有的配置表必须遵照此类型定义的字段名称
 *
 * @author Darkholme
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PoolSelectorElement {

    @XmlElement
    private long id;
    @XmlElement
    private long amount;
    @XmlElement
    private double weight;

    private Object payload;

    public PoolSelectorElement(long id, long amount, double weight) {
        this(id, amount, weight, null);
    }

    public PoolSelectorResult toResult() {
        return new PoolSelectorResult(id, amount, payload);
    }
}
