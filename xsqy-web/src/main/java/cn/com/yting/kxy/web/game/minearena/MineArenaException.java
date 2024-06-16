/*
 * Created 2018-10-18 15:55:46
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class MineArenaException extends KxyWebException {

    public static final int EC_ALREADY_CREATED = 1600;
    public static final int EC_PLV_NOT_MEET_REQUIREMENT = 1601;
    public static final int EC_REWARD_ALREADY_DELIVERED = 1602;
    public static final int EC_INSUFFICIENT_CHALLENGE_POINT = 1603;
    public static final int EC_LOCKED = 1604;
    public static final int EC_INSUFFICIENT_XS = 1605;

    public MineArenaException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static MineArenaException alreadyCreated() {
        return new MineArenaException(EC_ALREADY_CREATED, "已经创建过竞技场记录");
    }

    public static MineArenaException playerLevelNotMeetRequirement() {
        return new MineArenaException(EC_PLV_NOT_MEET_REQUIREMENT, "角色等级未达到要求");
    }

    public static MineArenaException rewardAlreadyDelivered() {
        return new MineArenaException(EC_REWARD_ALREADY_DELIVERED, "已经领取过奖励");
    }

    public static MineArenaException insufficientChallengePoint() {
        return new MineArenaException(EC_INSUFFICIENT_CHALLENGE_POINT, "挑战点不足");
    }

    public static MineArenaException locked() {
        return new MineArenaException(EC_LOCKED, "自己或挑战目标正在进行战斗");
    }

    public static MineArenaException insufficientXS() {
        return new MineArenaException(EC_INSUFFICIENT_XS, "仙石不足");
    }

}
