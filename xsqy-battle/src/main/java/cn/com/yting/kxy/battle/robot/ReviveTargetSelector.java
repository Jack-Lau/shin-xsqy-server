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
public class ReviveTargetSelector implements TargetSelector {

    @Override
    public Unit select(Unit source, List<Unit> allUnits) {
        return allUnits.stream()
                .filter(unit -> source.isAlly(unit))
                .filter(unit -> unit.isHpZero())
                .filter(unit -> !unit.isFlyable())
                .sorted((u1, u2) -> u2.getHp().getUpperLimit().compareTo(u1.getHp().getUpperLimit()))
                .findFirst()
                .orElse(null);
    }

}
