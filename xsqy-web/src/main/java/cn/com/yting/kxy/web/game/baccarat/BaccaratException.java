/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class BaccaratException extends KxyWebException {

    public static final int EC_活动未开启 = 3500;
    public static final int EC_角色等级不足 = 3501;
    public static final int EC_不是下注阶段 = 3502;
    public static final int EC_超出单局投注限制 = 3503;

    public BaccaratException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static BaccaratException 活动未开启() {
        return new BaccaratException(EC_活动未开启, "活动未开启");
    }

    public static BaccaratException 角色等级不足() {
        return new BaccaratException(EC_角色等级不足, "角色等级不足");
    }

    public static BaccaratException 不是下注阶段() {
        return new BaccaratException(EC_不是下注阶段, "不是下注阶段");
    }

    public static BaccaratException 超出单局投注限制() {
        return new BaccaratException(EC_超出单局投注限制, "超出单局投注限制");
    }

}
