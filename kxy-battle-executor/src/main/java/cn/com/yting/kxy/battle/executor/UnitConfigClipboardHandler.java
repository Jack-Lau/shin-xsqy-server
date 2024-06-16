/*
 * Created 2017-5-11 18:12:15
 */
package cn.com.yting.kxy.battle.executor;

import cn.com.yting.kxy.battle.executor.model.UnitModel;

/**
 *
 * @author Azige
 */
public interface UnitConfigClipboardHandler{

    void copy(UnitModel unitModel);

    UnitModel paste();
}
