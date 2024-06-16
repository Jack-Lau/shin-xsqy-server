/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Administrator
 */
public class MineArenaRewardObtainEvent extends KxyWebEvent {

    private final long accountId;

    public MineArenaRewardObtainEvent(Object source, long accountId) {
        super(source);
        this.accountId = accountId;
    }

    /**
     * @return the accountId
     */
    public long getAccountId() {
        return accountId;
    }

}
