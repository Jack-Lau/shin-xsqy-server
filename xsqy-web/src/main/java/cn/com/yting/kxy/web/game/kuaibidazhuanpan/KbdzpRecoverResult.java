/*
 * Created 2018-7-28 10:49:37
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class KbdzpRecoverResult {

    private KbdzpRecord record;
    private CurrencyRecord currencyRecord;
    private long timeToNextEnergy;
}
