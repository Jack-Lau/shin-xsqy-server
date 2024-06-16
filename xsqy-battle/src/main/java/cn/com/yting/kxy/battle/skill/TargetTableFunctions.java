/*
 * Created 2018-8-18 17:07:21
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.Unit;

/**
 *
 * @author Azige
 */
public class TargetTableFunctions {

    public static boolean 敌方_活着的(Unit source, Unit target) {
        return !source.isAlly(target) && !target.isHpZero();
    }

    public static boolean 敌方_死亡的(Unit source, Unit target) {
        return !source.isAlly(target) && target.isHpZero();
    }

    public static boolean 友方_活着的(Unit source, Unit target) {
        return source.isAlly(target) && !target.isHpZero();
    }

    public static boolean 友方_死亡的(Unit source, Unit target) {
        return source.isAlly(target) && target.isHpZero();
    }

    public static boolean 敌方(Unit source, Unit target) {
        return !source.isAlly(target);
    }

    public static boolean 友方(Unit source, Unit target) {
        return source.isAlly(target);
    }
}
