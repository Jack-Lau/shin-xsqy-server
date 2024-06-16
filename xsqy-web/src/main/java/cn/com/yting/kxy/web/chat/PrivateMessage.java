/*
 * Created 2018-8-6 16:21:02
 */
package cn.com.yting.kxy.web.chat;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.chat.model.ChatElement;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "private_message", indexes = {
    @Index(columnList = "receiver_account_id, sender_account_id"),
    @Index(columnList = "conversation")
})
@Data
public class PrivateMessage implements Serializable {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "sender_account_id", nullable = false)
    private long senderAccountId;
    @Column(name = "receiver_account_id", nullable = false)
    private long receiverAccountId;
    /**
     * 用作两个用户间的会话的索引，格式是 {@code "<较小的账号id>_<较大的账号id>"}
     */
    @Column(name = "conversation", nullable = false)
    private String conversation;
    @Column(name = "content", nullable = false, length = 5000)
    private String content;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Column(name = "already_read", nullable = false)
    private boolean alreadyRead = false;

    public void applyConversation() {
        conversation = createConversationString(senderAccountId, receiverAccountId);
    }

    public ChatMessage toChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(String.valueOf(id));
        chatMessage.setEventTime(eventTime);
        chatMessage.setSenderId(senderAccountId);
        chatMessage.setBroadcast(false);
        chatMessage.setReceiverId(receiverAccountId);
        try {
            chatMessage.setElements(objectMapper.readValue(
                content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ChatElement.class)
            ));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return chatMessage;
    }

    public static PrivateMessage createFromChatMessage(ChatMessage chatMessage) {
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setSenderAccountId(chatMessage.getSenderId());
        privateMessage.setReceiverAccountId(chatMessage.getReceiverId());
        privateMessage.applyConversation();
        try {
            privateMessage.setContent(objectMapper.writeValueAsString(chatMessage.getElements()));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        privateMessage.setEventTime(chatMessage.getEventTime());
        return privateMessage;
    }

    public static String createConversationString(long accountId, long anotherAccountId) {
        return "" + Math.min(accountId, anotherAccountId) + "_" + Math.max(accountId, anotherAccountId);
    }
}
