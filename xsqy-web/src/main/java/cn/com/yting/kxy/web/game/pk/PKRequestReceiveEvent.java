/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.pk;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.player.PlayerDetail;

/**
 *
 * @author Administrator
 */
@WebMessageType
public class PKRequestReceiveEvent extends KxyWebEvent {

    private final PlayerDetail sender;
    private final PlayerDetail receiver;
    private boolean isOK;
    private BattleResponse singleResponse;
    private BattleResponse multiResponse;

    public PKRequestReceiveEvent(Object source, PlayerDetail sender, PlayerDetail receiver, boolean isOK, BattleResponse singleResponse, BattleResponse multiResponse) {
        super(source);
        this.sender = sender;
        this.receiver = receiver;
        this.isOK = isOK;
        this.singleResponse = singleResponse;
        this.multiResponse = multiResponse;
    }

    /**
     * @return the sender
     */
    public PlayerDetail getSender() {
        return sender;
    }

    /**
     * @return the receiver
     */
    public PlayerDetail getReceiver() {
        return receiver;
    }

    /**
     * @return the isOK
     */
    public boolean isIsOK() {
        return isOK;
    }

    /**
     * @return the singleResponse
     */
    public BattleResponse getSingleResponse() {
        return singleResponse;
    }

    /**
     * @return the multiResponse
     */
    public BattleResponse getMultiResponse() {
        return multiResponse;
    }

}
