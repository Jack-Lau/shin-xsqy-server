/*
 * Created 2018-11-8 12:05:54
 */
package cn.com.yting.kxy.web.title;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class TitleException extends KxyWebException {

    public static final int EC_NOT_OWNER = -1;
    public static final int EC_NO_SUCH_RECIPE = -1;
    public static final int EC_LIMITED_QUANTITY_REACH = -1;

    public TitleException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static TitleException notOwner() {
        throw new TitleException(EC_NOT_OWNER, "不是称号的所有者");
    }

    public static TitleException noSuchRecipe() {
        throw new TitleException(EC_NO_SUCH_RECIPE, "不能用指定的货币兑换称号");
    }

    public static TitleException limitedQuantityReach() {
        throw new TitleException(EC_LIMITED_QUANTITY_REACH, "达到限量上限");
    }

}
