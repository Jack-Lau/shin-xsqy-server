/*
 * Created 2016-11-21 18:42:44
 */
package cn.com.yting.kxy.web.award.model;

import cn.com.yting.kxy.core.KxyConstants;
import cn.com.yting.kxy.core.QuadraticFunction;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class AwardBaseElement implements AwardElement {

    private QuadraticFunction expFunction;
    private QuadraticFunction goldFunction;

    public AwardBaseElement(QuadraticFunction expFunction) {
        this.expFunction = expFunction;
        this.goldFunction = new QuadraticFunction(0, 0, 0);
    }

    public AwardBaseElement(QuadraticFunction expFunction, QuadraticFunction goldFunction) {
        this.expFunction = expFunction;
        this.goldFunction = goldFunction;
    }

    @Override
    public void apply(AwardBuilder builder, int playerLevel, long playerFc) {
        builder.addExp((int) expFunction.applyAsDouble(playerLevel));
        builder.addCurrencyChance(KxyConstants.CURRENCY_GOLD_ID, (int) goldFunction.applyAsDouble(playerFc / 100));
    }
    
}
