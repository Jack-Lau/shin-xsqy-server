/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Data
@WebMessageType
public class PlayerOnlineStatus {

    public enum Status {
        OFFLINE, IDLE, BATTLE, MINIGAME
    }

    private PlayerLocation playerLocation;
    private Status status;

    public PlayerOnlineStatus(PlayerLocation playerLocation, Status status) {
        this.playerLocation = playerLocation;
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public static Status getStatus(int status) {
        switch (status) {
            case 0:
                return Status.OFFLINE;
            case 1:
                return Status.IDLE;
            case 2:
                return Status.BATTLE;
            case 3:
                return Status.MINIGAME;
            default:
                return Status.OFFLINE;
        }
    }

}
