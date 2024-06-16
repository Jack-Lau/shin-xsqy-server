/*
 * Created 2018-7-9 16:23:07
 */
package cn.com.yting.kxy.web.invitation;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class InvitationException extends KxyWebException {

    public static final int EC_INVITER_RECORD_EXISTED = 400;
    public static final int EC_INVITATION_CODE_NOT_VALID = 401;
    public static final int EC_INVIER_REACH_LIMIT = 402;
    public static final int EC_ALREADY_DELIVERED = 403;
    public static final int EC_INVITER_RECORD_NOT_EXISTED = 404;
    public static final int EC_INVITATION_LIMIT_REACH_MAX = 405;

    public InvitationException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static InvitationException inviterRecordExisted() {
        return new InvitationException(EC_INVITER_RECORD_EXISTED, "邀请者记录已存在");
    }

    public static InvitationException invitationCodeNotValid() {
        return new InvitationException(EC_INVITATION_CODE_NOT_VALID, "邀请码无效");
    }

    public static InvitationException inviterReachLimit() {
        return new InvitationException(EC_INVIER_REACH_LIMIT, "邀请者已达到邀请数量限制");
    }

    public static InvitationException invitationRewardAlreadyDelivered() {
        return new InvitationException(EC_ALREADY_DELIVERED, "邀请奖励已经领取过");
    }

    public static InvitationException inviterRecordNotExisted() {
        return new InvitationException(EC_INVITER_RECORD_NOT_EXISTED, "邀请者记录不存在");
    }

    public static InvitationException invitationLimitReachMax() {
        return new InvitationException(EC_INVITATION_LIMIT_REACH_MAX, "可邀请数已达到最大");
    }
}
