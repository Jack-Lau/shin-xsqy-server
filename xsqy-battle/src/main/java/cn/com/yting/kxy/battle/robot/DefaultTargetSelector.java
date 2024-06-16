/*
 * Created 2015-10-21 10:43:40
 */
package cn.com.yting.kxy.battle.robot;

import java.util.List;

import cn.com.yting.kxy.battle.Unit;

/**
 *
 * @author Azige
 */
public class DefaultTargetSelector implements TargetSelector {

    @Override
    public Unit select(Unit source, List<Unit> allUnits) {
        return allUnits.stream()
                .filter(unit -> !source.isAlly(unit))
                .filter(unit -> !unit.isHpZero())
                .sorted((u1, u2) -> u1.getHp().compareTo(u2.getHp()))
                .findFirst()
                .orElse(null);
    }

}
