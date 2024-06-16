/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.pk;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.player.PlayerDetail;

/**
 *
 * @author Administrator
 */
@WebMessageType
public class PKRequestSendEvent extends KxyWebEvent {

    private final PlayerDetail sender;
    private final PlayerDetail receiver;

    public PKRequestSendEvent(Object source, PlayerDetail sender, PlayerDetail receiver) {
        super(source);
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * @return the sender
     */
    public PlayerDetail getSender() {
        return sender;
    }

    /**
     * @return the receiverId
     */
    public PlayerDetail getReceiver() {
        return receiver;
    }

}
