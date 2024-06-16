/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class FashionException extends KxyWebException {

    public static final int EC_NO_SUCH_RECIPE = 2700;
    public static final int EC_LIMITED_QUANTITY_REACH = 2701;
    public static final int EC_DYE_NAME_ILLEGAL = 2702;

    public FashionException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static FashionException noSuchRecipe() {
        return new FashionException(EC_NO_SUCH_RECIPE, "不能用指定的货币兑换时装");
    }

    public static FashionException limitedQuantityReach() {
        return new FashionException(EC_LIMITED_QUANTITY_REACH, "达到限量上限");
    }

    public static FashionException dyeNameIllegal() {
        return new FashionException(EC_DYE_NAME_ILLEGAL, "染色方案包含屏蔽词");
    }

}
