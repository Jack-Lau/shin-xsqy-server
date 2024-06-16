/*
 * Created 2015-10-21 10:43:40
 */
package cn.com.yting.kxy.battle.robot;

import java.util.List;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class RandomTargetSelector implements TargetSelector {

    @Override
    public Unit select(Unit source, List<Unit> allUnits) {
        Unit[] options = allUnits.stream()
                .filter(unit -> !source.isAlly(unit))
                .toArray(Unit[]::new);
        if (options.length > 0) {
            return options[RandomProvider.getRandom().nextInt(options.length)];
        } else {
            return null;
        }
    }

}
