/*
 * Created 2015-11-2 17:21:25
 */
package cn.com.yting.kxy.battle.robot;

import java.util.List;

import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.core.resource.Resource;

/**
 *
 * @author Azige
 */
public interface Robot extends Resource {

    @Override
    long getId();

    Action generateActionAtTurnStart(Unit source, List<Unit> allUnits);

    Action generateActionAtActionStart(Unit source, List<Unit> allUnits);

}
