/*
 * Created 2015-10-21 10:41:31
 */
package cn.com.yting.kxy.battle.robot;

import java.io.Serializable;
import java.util.List;

import cn.com.yting.kxy.battle.Unit;

/**
 *
 * @author Azige
 */
public interface TargetSelector extends Serializable {

    Unit select(Unit source, List<Unit> allUnits);

}
