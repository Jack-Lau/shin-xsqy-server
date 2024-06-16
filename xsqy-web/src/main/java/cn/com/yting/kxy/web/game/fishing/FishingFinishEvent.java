/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Administrator
 */
public class FishingFinishEvent extends KxyWebEvent {

    private final FishingRecord fishingRecord;

    public FishingFinishEvent(Object source, FishingRecord fishingRecord) {
        super(source);
        this.fishingRecord = fishingRecord;
    }

    /**
     * @return the fishingRecord
     */
    public FishingRecord getFishingRecord() {
        return fishingRecord;
    }

}
