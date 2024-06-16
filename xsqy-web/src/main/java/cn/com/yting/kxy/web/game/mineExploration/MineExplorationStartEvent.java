/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class MineExplorationStartEvent extends KxyWebEvent {

    private final MineExplorationRecord record;

    public MineExplorationStartEvent(Object source, MineExplorationRecord record) {
        super(source);
        this.record = record;
    }

    /**
     * @return the record
     */
    public MineExplorationRecord getRecord() {
        return record;
    }

}
