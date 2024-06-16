/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.price;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class PriceReduceEvent extends KxyWebEvent {

    private final PriceRecord priceRecord;

    public PriceReduceEvent(Object source, PriceRecord priceRecord) {
        super(source);
        this.priceRecord = priceRecord;
    }

    /**
     * @return the priceRecord
     */
    public PriceRecord getPriceRecord() {
        return priceRecord;
    }

}
