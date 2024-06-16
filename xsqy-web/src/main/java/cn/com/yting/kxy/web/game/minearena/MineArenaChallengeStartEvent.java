/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class MineArenaChallengeStartEvent extends KxyWebEvent {

    private final MineArenaRecord mineArenaRecord;

    public MineArenaChallengeStartEvent(Object source, MineArenaRecord mineArenaRecord) {
        super(source);
        this.mineArenaRecord = mineArenaRecord;
    }

    /**
     * @return the mineArenaRecord
     */
    public MineArenaRecord getMineArenaRecord() {
        return mineArenaRecord;
    }

}
