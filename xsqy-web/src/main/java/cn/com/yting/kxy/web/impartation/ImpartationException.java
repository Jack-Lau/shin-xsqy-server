/*
 * Created 2018-11-25 5:07:59
 */
package cn.com.yting.kxy.web.impartation;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class ImpartationException extends KxyWebException {

    public static final int EC_NOT_MASTER = 2200;
    public static final int EC_TARGET_NOT_DISCIPLE = 2201;
    public static final int EC_ALREADY_IN_DISCIPLINE = 2202;
    public static final int EC_MAX_DISCIPLE_COUNT_REACHED = 2203;
    public static final int EC_MASTER_NOT_IDLE = 2204;
    public static final int EC_DISCIPLE_NOT_IDLE = 2205;
    public static final int EC_当前未到能够领取的时间 = 2206;

    public ImpartationException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static ImpartationException notMaster() {
        throw new ImpartationException(EC_NOT_MASTER, "自己不是师父身份");
    }

    public static ImpartationException targetNotDisciple() {
        throw new ImpartationException(EC_TARGET_NOT_DISCIPLE, "对方不是徒弟身份");
    }

    public static ImpartationException alreadyInDiscipline() {
        throw new ImpartationException(EC_ALREADY_IN_DISCIPLINE, "对方已经拜师");
    }

    public static ImpartationException maxDiscipleCountReached() {
        throw new ImpartationException(EC_MAX_DISCIPLE_COUNT_REACHED, "目标已达到最大徒弟数");
    }

    public static ImpartationException masterNotIdle() {
        throw new ImpartationException(EC_MASTER_NOT_IDLE, "师父的登录时间未达到要求");
    }

    public static ImpartationException discipleNotIdle() {
        throw new ImpartationException(EC_DISCIPLE_NOT_IDLE, "徒弟的登录时间未达到要求");
    }

    public static ImpartationException 当前未到能够领取的时间() {
        throw new ImpartationException(EC_当前未到能够领取的时间, "当前未到能够领取的时间");
    }
}
