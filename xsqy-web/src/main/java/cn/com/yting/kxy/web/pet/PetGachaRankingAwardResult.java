/*
 * Created 2018-10-13 18:57:12
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PetGachaRankingAwardResult {

    private Pet pet;
    private CurrencyStack currencyStack;
}
