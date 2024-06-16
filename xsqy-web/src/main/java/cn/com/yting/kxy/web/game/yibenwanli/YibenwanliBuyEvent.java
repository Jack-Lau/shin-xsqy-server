/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class YibenwanliBuyEvent extends KxyWebEvent {

    private final YibenwanliRecord yibenwanliRecord;

    public YibenwanliBuyEvent(Object source, YibenwanliRecord yibenwanliRecord) {
        super(source);
        this.yibenwanliRecord = yibenwanliRecord;
    }

    /**
     * @return the yibenwanliRecord
     */
    public YibenwanliRecord getYibenwanliRecord() {
        return yibenwanliRecord;
    }

}
