/*
 * Created 2018-8-1 11:43:54
 */
package cn.com.yting.kxy.web.chat;

import cn.com.yting.kxy.web.KxyWebEvent;
import cn.com.yting.kxy.web.chat.model.ChatMessage;

/**
 *
 * @author Azige
 */
public class ChatMessageSentEvent extends KxyWebEvent {

    private final ChatMessage chatMessage;

    public ChatMessageSentEvent(Object source, ChatMessage chatMessage) {
        super(source);
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
