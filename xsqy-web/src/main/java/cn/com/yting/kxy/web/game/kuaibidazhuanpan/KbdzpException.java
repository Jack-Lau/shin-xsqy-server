/*
 * Created 2018-7-6 18:53:19
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class KbdzpException extends KxyWebException {

    public static final int EC_INSUFFICIENT_ENERGY = 300;
    public static final int EC_ACODE_NOT_VALID = 301;
    public static final int EC_BOOSTER_ALREADY_ENABLED = 302;
    public static final int EC_INVITEE_BONUS_NOT_AVAILABLE = 303;
    public static final int EC_PENDING_AWARD_EXISTED = 304;
    public static final int EC_PENDING_AWARD_NOT_EXISTED = 305;
    public static final int EC_TODAY_TURN_COUNT_REACH_LIMIT = 306;

    public KbdzpException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static KbdzpException insufficientEnergy(int value) {
        return new KbdzpException(EC_INSUFFICIENT_ENERGY, "能量值不足：" + value);
    }

    public static KbdzpException acodeNotValid() {
        return new KbdzpException(EC_ACODE_NOT_VALID, "激活码不正确");
    }

    public static KbdzpException boosterAlreadyEnabled() {
        return new KbdzpException(EC_BOOSTER_ALREADY_ENABLED, "激活码已使用过");
    }

    public static KbdzpException inviteeBonusNotAvailable() {
        return new KbdzpException(EC_INVITEE_BONUS_NOT_AVAILABLE, "邀请奖励不可用");
    }

    public static KbdzpException pendingAwardExisted() {
        return new KbdzpException(EC_PENDING_AWARD_EXISTED, "当前存在未领取的奖励");
    }

    public static KbdzpException pendingAwardNotExisted() {
        return new KbdzpException(EC_PENDING_AWARD_EXISTED, "当前不存在未领取的奖励");
    }

    public static KbdzpException todayTurnCountReachLimit() {
        return new KbdzpException(EC_TODAY_TURN_COUNT_REACH_LIMIT, "本日转盘次数已达上限");
    }
}
