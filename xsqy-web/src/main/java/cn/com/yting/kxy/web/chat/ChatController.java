/*
 * Created 2018-8-1 11:54:42
 */
package cn.com.yting.kxy.web.chat;

import java.util.Collection;
import java.util.List;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/chat")
public class ChatController implements ModuleApiProvider {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private ChatService chatService;
    @Autowired
    private WebsocketMessageService websocketMessageService;
    @Autowired
    private CompositePlayerService compositePlayerService;

    @PostMapping(path = "/sendMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatMessage sendMessage(
            @AuthenticationPrincipal Account account,
            @RequestBody ChatMessage message
    ) {
        return chatService.sendPlayerMessage(account.getId(), message);
    }

    @RequestMapping("/public/view")
    public Collection<ChatMessage> viewPublicMessages() {
        return chatService.getLatestMessages();
    }

    @RequestMapping("/private/incomingInfo")
    public List<PrivateMessageIncomingInfo> getIncomingInfoList(@AuthenticationPrincipal Account account) {
        return privateMessageRepository.findPrivateMessageIncomings(account.getId());
    }

    @RequestMapping("/private/conversation")
    public List<ChatMessage> getConversation(
            @AuthenticationPrincipal Account account,
            @RequestParam("anotherAccountId") long anotherAccountId
    ) {
        return chatService.getConversation(account.getId(), anotherAccountId);
    }

    @PostMapping("/private/markAlreadyRead")
    public WebMessageWrapper markAlreadyRead(
            @AuthenticationPrincipal Account account,
            @RequestParam("senderAccountId") long senderAccountId
    ) {
        chatService.markAlreadyRead(senderAccountId, account.getId());
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/latestInterestingMessage/{id}")
    public List<ChatMessage> getLatestInterestingMessages(@PathVariable("id") long broadcastId) {
        return chatService.getInterestingMessages(broadcastId);
    }

    @TransactionalEventListener
    public void onChatMessageSent(ChatMessageSentEvent event) {
        ChatMessage message = event.getChatMessage();
        ChatMessageComplex complex = new ChatMessageComplex(message, compositePlayerService.getPlayerBaseInfo(message.getSenderId()));
        if (message.isBroadcast()) {
            websocketMessageService.sendToAll("/chat/message", complex);
        } else {
            websocketMessageService.sendToUser(message.getSenderId(), "/chat/message/private", complex);
            websocketMessageService.sendToUser(message.getReceiverId(), "/chat/message/private", complex);
        }
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("chat")
                .baseUri("/chat")
                //
                .webInterface()
                .uri("/sendMessage")
                .post()
                .description("发送一条消息")
                .requestBody(ChatMessage.class)
                .response(ChatMessage.class, "处理后实际发送的消息")
                .and()
                //
                .webInterface()
                .name("public_view")
                .uri("/public/view")
                .description("获取最近的公共消息")
                .responseArray(ChatMessage.class, "最近的公共消息的集合")
                .and()
                //
                .webInterface()
                .name("private_incomingInfo")
                .uri("/private/incomingInfo")
                .description("获取未读消息的信息")
                .responseArray(PrivateMessageIncomingInfo.class, "未读消息的信息的集合")
                .and()
                //
                .webInterface()
                .name("private_conversation")
                .uri("/private/conversation")
                .description("获取与一个指定的账号相关的收发的消息")
                .requestParameter("integer", "anotherAccountId", "要查询的聊天对象的账号id")
                .requestPagenationParameters()
                .responseArray(ChatMessage.class, "对应的消息集合")
                .and()
                //
                .webInterface()
                .name("private_markAlreadyRead")
                .uri("/private/markAlreadyRead")
                .post()
                .description("将一个发送者发送给自己的所有消息标记为已读")
                .requestParameter("integer", "senderAccountId", "要操作的发送者账号id")
                .and()
                //
                .webInterface()
                .name("viewLatestInterestingMessages")
                .uri("/latestInterestingMessage/{id}")
                .description("获得指定的最近的界面广播记录")
                .requestParameter("number", "id", "广播id")
                .responseArray(ChatMessage.class, "对应的消息集合")
                .and()
                //
                //
                .webNotification()
                .topic("/chat/message")
                .description("有新的公共消息的通知")
                .messageType(ChatMessageComplex.class)
                .and()
                //
                .webNotification()
                .queue("/chat/message/private")
                .description("有新的私人消息的通知")
                .messageType(ChatMessageComplex.class)
                .and();
    }

}
