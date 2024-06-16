/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class BaccaratLotteryEvent extends KxyWebEvent {

    private final BaccaratBet baccaratBet;

    public BaccaratLotteryEvent(Object source, BaccaratBet baccaratBet) {
        super(source);
        this.baccaratBet = baccaratBet;
    }

    /**
     * @return the baccaratBet
     */
    public BaccaratBet getBaccaratBet() {
        return baccaratBet;
    }

}
