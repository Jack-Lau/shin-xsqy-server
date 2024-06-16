/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class FriendApplyEvent extends KxyWebEvent {

    private final long targetId;
    private final Friend actor;

    public FriendApplyEvent(Object source, long targetId, Friend actor) {
        super(source);
        this.targetId = targetId;
        this.actor = actor;
    }

    /**
     * @return the targetId
     */
    public long getTargetId() {
        return targetId;
    }

    /**
     * @return the actor
     */
    public Friend getActor() {
        return actor;
    }

}
