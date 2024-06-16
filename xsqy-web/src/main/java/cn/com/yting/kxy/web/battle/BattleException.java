/*
 * Created 2018-8-16 17:31:00
 */
package cn.com.yting.kxy.web.battle;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class BattleException extends KxyWebException {

    public BattleException(int errorCode, String message) {
        super(errorCode, message);
    }

}
