/*
 * Created 2018-8-3 16:58:05
 */
package cn.com.yting.kxy.web.award.model;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class AwardCurrencyElement implements AwardElement {

    private long currencyId;
    private long amount;

    @Override
    public void apply(AwardBuilder builder, int playerLevel, long playerFc) {
        builder.addCurrencyChance(currencyId, amount);
    }
}
