/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class ChanglefangException extends KxyWebException {

    public static final int EC_玩法未开启 = 3200;
    public static final int EC_角色等级不足 = 3201;
    public static final int EC_至少购买1张本票 = 3202;
    public static final int EC_至少兑换1个块币 = 3203;

    public ChanglefangException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static ChanglefangException 玩法未开启() {
        return new ChanglefangException(EC_玩法未开启, "玩法未开启");
    }

    public static ChanglefangException 角色等级不足() {
        return new ChanglefangException(EC_角色等级不足, "角色等级不足");
    }

    public static ChanglefangException 至少购买1张本票() {
        return new ChanglefangException(EC_至少购买1张本票, "至少购买1张本票");
    }

    public static ChanglefangException 至少兑换1个块币() {
        return new ChanglefangException(EC_至少兑换1个块币, "至少兑换1个块币");
    }

}
