/*
 * Created 2015-10-9 11:55:19
 */
package cn.com.yting.kxy.battle.handlers;

import java.util.Random;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.event.UnitEvent;
import cn.com.yting.kxy.battle.event.UnitEvent.UnitEventType;
import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import io.github.azige.mgxy.event.EventHandler;
import io.github.azige.mgxy.event.EventType;

/**
 * 速度随机化处理器
 *
 * @author Azige
 */
public class SpeedRandomizeHandler implements EventHandler<UnitEvent> {

    /**
     * 随机化波动率，现在是 7%
     */
    private static final double RAND_RATE = 0.07D;

    @Override
    public void handle(UnitEvent event) {
        Unit unit = event.getUnit();
        Random random = RandomProvider.getRandom();
        double rand = RAND_RATE * (random.nextDouble() * 2 - 1);
        SimpleParameterSpace parameterSpace = new SimpleParameterSpace();
        parameterSpace.addParameterBase(ParameterNameConstants.速度, new SimpleParameterBase(unit.getSpeed() * rand));
        unit.setExtraParameterSpace(new AggregateParameterSpace(unit.getExtraParameterSpace(), parameterSpace));
    }

    @Override
    public EventType<UnitEvent> getHandleEventType() {
        return UnitEventType.UNIT_ATTENDING;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
