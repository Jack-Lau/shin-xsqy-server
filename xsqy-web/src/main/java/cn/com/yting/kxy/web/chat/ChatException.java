/*
 * Created 2018-8-4 15:54:20
 */
package cn.com.yting.kxy.web.chat;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class ChatException extends KxyWebException {

    public static final int EC_ILLEGAL_ELEMENT_TYPE = 800;
    public static final int EC_ILLEGAL_RECEIVER = 801;
    public static final int EC_OVER_FREQUENCY_LIMIT = 802;

    public ChatException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static ChatException illegalElementType(String typeName) {
        throw new ChatException(EC_ILLEGAL_ELEMENT_TYPE, "无效的消息元素类型：" + typeName);
    }

    public static ChatException illegalReceiver(String reason) {
        throw new ChatException(EC_ILLEGAL_RECEIVER, "无效的接收者：" + reason);
    }

    public static ChatException overFrequencyLimit() {
        throw new ChatException(EC_OVER_FREQUENCY_LIMIT, "超过消息发送频率限制");
    }
}
