/*
 * Created 2018-9-20 16:40:30
 */
package cn.com.yting.kxy.web.recycling;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class RecyclingResult {

    private long sourceId;
    private CurrencyStack currencyStack;
    private boolean bingo;
}
