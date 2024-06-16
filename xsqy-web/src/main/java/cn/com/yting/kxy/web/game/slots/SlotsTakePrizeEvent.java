/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class SlotsTakePrizeEvent extends KxyWebEvent {

    private final SlotsRecord slotsRecord;

    public SlotsTakePrizeEvent(Object source, SlotsRecord slotsRecord) {
        super(source);
        this.slotsRecord = slotsRecord;
    }

    /**
     * @return the slotsRecord
     */
    public SlotsRecord getSlotsRecord() {
        return slotsRecord;
    }

}
