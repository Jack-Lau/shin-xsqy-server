/*
 * Created 2018-10-11 12:53:35
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class PetException extends KxyWebException {

    public static final int EC_NOT_OWNER = 1400;
    public static final int EC_NO_AWARD = 1401;
    public static final int EC_NOT_IN_CANDIDATE = 1402;
    public static final int EC_SOUL_LEVEL_REACH_MAX = 1403;
    public static final int EC_INSUFFICIENT_PET_COLOR = 1404;
    public static final int EC_INSUFFICIENT_195 = 1405;
    public static final int EC_INSUFFICIENT_YB = 1406;

    public PetException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static PetException notOwner() {
        return new PetException(EC_NOT_OWNER, "不是宠物的所有者");
    }

    public static PetException noAward() {
        return new PetException(EC_NO_AWARD, "没有可领取的奖励");
    }

    public static PetException notInCandidate() {
        return new PetException(EC_NOT_IN_CANDIDATE, "要学习的技能不在候选中");
    }

    public static PetException soulLevelReachMax() {
        return new PetException(EC_SOUL_LEVEL_REACH_MAX, "附魂等级已达上限");
    }

    public static PetException insufficientPetColor() {
        return new PetException(EC_INSUFFICIENT_PET_COLOR, "宠物品质不足");
    }

    public static PetException insufficient195() {
        return new PetException(EC_INSUFFICIENT_195, "魂晶不足");
    }

    public static PetException insufficientYB() {
        throw new PetException(EC_INSUFFICIENT_YB, "元宝不足");
    }

}
