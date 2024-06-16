/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class BrawlEndEvent extends KxyWebEvent {

    private final BrawlRecord brawlRecord;

    public BrawlEndEvent(Object source, BrawlRecord brawlRecord) {
        super(source);
        this.brawlRecord = brawlRecord;
    }

    /**
     * @return the brawlRecord
     */
    public BrawlRecord getBrawlRecord() {
        return brawlRecord;
    }

}
