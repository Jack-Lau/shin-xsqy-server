/*
 * Created 2018-9-22 15:41:36
 */
package cn.com.yting.kxy.web.equipment;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class EquipmentException extends KxyWebException {

    public static final int EC_ENHANCE_LEVEL_REACH_MAX = 1500;
    public static final int EC_INSUFFICIENT_CURRENCY = 1501;
    public static final int EC_SOUL_LEVEL_REACH_MAX = 1502;
    public static final int EC_INSUFFICIENT_EQUIPMENT_COLOR = 1503;
    public static final int EC_INSUFFICIENT_195 = 1504;
    public static final int EC_CANNOT_SOUL_THIS_PART = 1505;
    public static final int EC_INSUFFICIENT_YB = 1506;

    public EquipmentException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static EquipmentException enhanceLevelReachMax() {
        return new EquipmentException(EC_ENHANCE_LEVEL_REACH_MAX, "强化已到达上限");
    }

    public static EquipmentException insufficientCurrency() {
        throw new EquipmentException(EC_INSUFFICIENT_CURRENCY, "货币不足");
    }

    public static EquipmentException soulLevelReachMax() {
        throw new EquipmentException(EC_SOUL_LEVEL_REACH_MAX, "附魂等级已达上限");
    }

    public static EquipmentException insufficientEquipmentColor() {
        throw new EquipmentException(EC_INSUFFICIENT_EQUIPMENT_COLOR, "装备品质不足");
    }

    public static EquipmentException insufficient195() {
        throw new EquipmentException(EC_INSUFFICIENT_195, "魂晶不足");
    }

    public static EquipmentException cannotSoulThisPart() {
        throw new EquipmentException(EC_CANNOT_SOUL_THIS_PART, "该装备部位无法附魂");
    }

    public static EquipmentException insufficientYB() {
        throw new EquipmentException(EC_INSUFFICIENT_YB, "元宝不足");
    }

}
