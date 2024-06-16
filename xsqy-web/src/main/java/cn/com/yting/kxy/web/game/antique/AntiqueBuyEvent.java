/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class AntiqueBuyEvent extends KxyWebEvent {

    private final AntiqueRecord antiqueRecord;

    public AntiqueBuyEvent(Object source, AntiqueRecord antiqueRecord) {
        super(source);
        this.antiqueRecord = antiqueRecord;
    }

    /**
     * @return the antiqueRecord
     */
    public AntiqueRecord getAntiqueRecord() {
        return antiqueRecord;
    }

}
