/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import cn.com.yting.kxy.web.game.secretShop.SecretShopRecord.Prize;
import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class SecretShopOverall {

    private SecretShopSharedRecord secretShopSharedRecord;
    private SecretShopRecord secretShopRecord;
    private List<Long> prices;
    private List<Prize> drawPrizes;

    public SecretShopOverall(SecretShopSharedRecord secretShopSharedRecord, SecretShopRecord secretShopRecord, List<Long> prices) {
        this.secretShopSharedRecord = secretShopSharedRecord;
        this.secretShopRecord = secretShopRecord;
        this.prices = prices;
        this.drawPrizes = null;
    }

    public SecretShopOverall(SecretShopSharedRecord secretShopSharedRecord, SecretShopRecord secretShopRecord, List<Long> prices, List<Prize> drawPrizes) {
        this.secretShopSharedRecord = secretShopSharedRecord;
        this.secretShopRecord = secretShopRecord;
        this.prices = prices;
        this.drawPrizes = drawPrizes;
    }

}
