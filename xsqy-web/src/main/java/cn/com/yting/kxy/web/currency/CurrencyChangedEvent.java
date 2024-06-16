/*
 * Created 2018-6-26 18:10:01
 */
package cn.com.yting.kxy.web.currency;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
@WebMessageType
public class CurrencyChangedEvent extends KxyWebEvent {

    private final long accountId;
    private final long currencyId;
    private final long beforeAmount;
    private final long afterAmount;
    private final Integer purpose;

    public CurrencyChangedEvent(Object source, long accountId, long currencyId, long beforeAmount, long afterAmount, Integer purpose) {
        super(source);
        this.accountId = accountId;
        this.currencyId = currencyId;
        this.beforeAmount = beforeAmount;
        this.afterAmount = afterAmount;
        this.purpose = purpose;
    }

}
