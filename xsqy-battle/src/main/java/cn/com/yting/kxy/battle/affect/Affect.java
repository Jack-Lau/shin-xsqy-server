/*
 * Created 2015-10-12 11:13:31
 */
package cn.com.yting.kxy.battle.affect;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.record.AffectRecord;

/**
 * 影响会直接改变单位的状态并生成记录
 *
 * @author Azige
 */
public interface Affect {

    AffectRecord affect(Unit target);
}
