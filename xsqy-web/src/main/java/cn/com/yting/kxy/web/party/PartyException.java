/*
 * Created 2018-9-27 10:55:21
 */
package cn.com.yting.kxy.web.party;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class PartyException extends KxyWebException {

    public static final int EC_INVALID_CANDIDATE = 1300;
    public static final int EC_LEVEL_REQUIREMENT_NOT_MEET = 1301;
    public static final int EC_MAX_SUPPORT_COUNT_REACHED = 1303;
    public static final int EC_MAX_PARTY_MEMBER_REACHED = 1304;
    public static final int EC_TARGET_ALREADY_IN_PARTY = 1305;
    public static final int EC_TARGET_IN_COOLDOWN = 1306;
    public static final int EC_INSUFFICIENT_CURRENCY = 1307;
    public static final int EC_NOT_IN_PARTY = 1308;
    public static final int EC_SUPPORT_REWARD_ALREADY_DELIVERED = 1309;

    public PartyException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static PartyException invalidCandidate() {
        return new PartyException(EC_INVALID_CANDIDATE, "被邀请者不是有效的候选人");
    }

    public static PartyException levelRequirementNotMeet() {
        return new PartyException(EC_LEVEL_REQUIREMENT_NOT_MEET, "角色等级未达到要求");
    }

    public static PartyException maxSupportCountReached() {
        return new PartyException(EC_MAX_SUPPORT_COUNT_REACHED, "被邀请者已达到最大助战数量");
    }

    public static PartyException maxPartyMemberReached() {
        return new PartyException(EC_MAX_PARTY_MEMBER_REACHED, "队伍已达最大人数");
    }

    public static PartyException targetAlreadyInParty() {
        return new PartyException(EC_TARGET_ALREADY_IN_PARTY, "被邀请者已经在队伍中");
    }

    public static PartyException targetInCooldown() {
        return new PartyException(EC_TARGET_IN_COOLDOWN, "再次邀请被邀请者的间隔时间未到");
    }

    public static PartyException insufficientCurrency() {
        return new PartyException(EC_INSUFFICIENT_CURRENCY, "货币不足");
    }

    public static PartyException notInParty() {
        return new PartyException(EC_NOT_IN_PARTY, "对象不在助战队伍中");
    }

    public static PartyException supportRewardAlreadyDelivered() {
        return new PartyException(EC_SUPPORT_REWARD_ALREADY_DELIVERED, "今天已经领取过助战奖励");
    }
}
