/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.random.pool;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 持有一次抽选所需要的公共池的和个人池的值。
 * 抽选一个元素时，若与其对应的池存在且个人池的值为正数、公共池的值大于等于该元素的值，
 * 此元素才会参与随机抽选，否则从抽选集合中排除
 *
 * @author Darkholme
 */
@Data
@AllArgsConstructor
public class PoolValueHolder {

    private long remainTotalPoolAmount;
    private long remainPersonalPoolAmount;

    public void decreaseRemainTotalPoolAmount(long value) {
        remainTotalPoolAmount -= value;
    }

    public void decreaseRemainPersonalPoolAmount(long value) {
        remainPersonalPoolAmount -= value;
    }
}
