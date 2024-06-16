/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.random.pool;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 表示一个抽选结果
 *
 * @author Darkholme
 */
@Value
@AllArgsConstructor
public class PoolSelectorResult {

    private long id;
    private long amount;
    private Object payload;

    public PoolSelectorResult(long id, long amount) {
        this(id, amount, null);
    }
}
