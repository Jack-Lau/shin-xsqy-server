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
public class FriendApplyPassEvent extends KxyWebEvent {

    private final long actorId;
    private final Friend target;

    public FriendApplyPassEvent(Object source, long actorId, Friend target) {
        super(source);
        this.actorId = actorId;
        this.target = target;
    }

    /**
     * @return the actorId
     */
    public long getActorId() {
        return actorId;
    }

    /**
     * @return the target
     */
    public Friend getTarget() {
        return target;
    }

}
