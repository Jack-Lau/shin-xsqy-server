/*
 * Created 2019-2-13 17:17:54
 */
package cn.com.yting.kxy.web.game.yuanxiaojiayao;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class YxjyException extends KxyWebException {

    public static final int EC_邀请时间间隔未到 = -1;
    public static final int EC_已经参加过指定玩家的邀请 = -1;
    public static final int EC_对方的邀请数量已满 = 3400;

    public YxjyException(int errorCode, String message) {
        super(errorCode, message);
    }
}
