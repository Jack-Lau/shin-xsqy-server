/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class ShopException extends KxyWebException {

    public static final int EC_COMMODITY_NOT_EXIST = 2300;
    public static final int EC_NOT_ALLOW_BATCH_BUY = 2301;
    public static final int EC_PRICE_NOT_MATCH = 2302;
    public static final int EC_INSUFFICIENT_REMAIN_COUNT = 2303;

    public ShopException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static ShopException commodityNotExist() {
        return new ShopException(EC_COMMODITY_NOT_EXIST, "商品不存在");
    }

    public static ShopException notAllowBatchBuy() {
        return new ShopException(EC_NOT_ALLOW_BATCH_BUY, "不允许批量购买");
    }

    public static ShopException priceNotMatch() {
        return new ShopException(EC_PRICE_NOT_MATCH, "当前售价发生变动");
    }

    public static ShopException insufficientRemainCount() {
        return new ShopException(EC_INSUFFICIENT_REMAIN_COUNT, "库存不足");
    }

}
