/*
 * Created 2018-7-19 17:16:20
 */
package cn.com.yting.kxy.web.chat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@WebMessageType
public class ChatMessage {

    /**
     * 消息的唯一 ID
     */
    private String id;

    /**
     * 广播消息的原型ID
     */
    private long broadcastId;

    /**
     * 消息发出的时间
     */
    private Date eventTime;

    /**
     * 表示这条消息是否是一个系统消息
     */
    private boolean systemMessage;

    /**
     * 根据是否是系统消息，此字段的语义是系统模块的id或用户的账号id
     */
    private long senderId;

    /**
     * 表示这条消息是否是一个广播消息
     */
    private boolean broadcast;

    /**
     * 表示接收者的账号id
     */
    private long receiverId;

    /**
     * 消息的元素集合
     */
    private List<ChatElement<?>> elements = new ArrayList<>();

    // TODO: 未来使用的一些额外参数，例如“前往”之类的描述信息
    public static ChatMessage createTemplateMessage(long templateId, Map<String, ?> args) {
        ChatMessage message = new ChatMessage();
        message.setBroadcastId(templateId);
        message.setElements(Collections.singletonList(new TemplateElement(
                templateId,
                args.entrySet().stream()
                        .collect(Collectors.toMap(entry -> entry.getKey(), entry -> String.valueOf(entry.getValue())))
        )));
        return message;
    }
}
