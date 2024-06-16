/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.robot;

import cn.com.yting.kxy.battle.Unit;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class RecoverTargetSelector implements TargetSelector {

    @Override
    public Unit select(Unit source, List<Unit> allUnits) {
        return allUnits.stream()
                .filter(unit -> source.isAlly(unit))
                .filter(unit -> !unit.isHpZero())
                .sorted((u1, u2) -> u1.getHp().getRate() > u2.getHp().getRate() ? 1 : -1)
                .findFirst()
                .orElse(null);
    }

}
