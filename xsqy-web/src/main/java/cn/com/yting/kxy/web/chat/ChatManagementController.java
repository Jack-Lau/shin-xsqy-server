/*
 * Created 2018-8-4 17:34:26
 */
package cn.com.yting.kxy.web.chat;

import java.util.Optional;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/management/chat")
public class ChatManagementController implements ModuleApiProvider {

    @Autowired
    private ChatService chatService;

    @PostMapping("/sendGameMasterMessage")
    public ChatMessage sendGameMasterMessage(
        @RequestParam(name = "serviceId", required = false) Long serviceId,
        @RequestBody ChatMessage chatMessage
    ) {
        if (serviceId == null) {
            serviceId = ChatConstants.SERVICE_ID_GAME_MASTER;
        }
        return chatService.sendSystemMessage(serviceId, chatMessage);
    }

    @RequestMapping("/sendingBroadcastState")
    public Optional<SendingBroadcastSetting> sendingBroadcastState() {
        return Optional.ofNullable(chatService.getSendingBroadcastState());
    }

    @PostMapping("/createSendingBroadcastTask")
    public SendingBroadcastSetting createSendingBroadcastTask(
        @RequestParam("message") String message,
        @RequestParam("interval") long interval
    ) {
        return chatService.createSendingBroadcastTask(message, interval);
    }

    @PostMapping("/cancleSendingBroadcastTask")
    public WebMessageWrapper cancleSendingBroadcastTask() {
        chatService.cancelSendingBroadcastTask();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("聊天管理")
            .baseUri("/management/chat")
            //
            .webInterface()
            .uri("/sendGameMasterMessage")
            .post()
            .description("发送一条公告")
            .requestParameter("number", "serviceId", "服务id（可选）")
            .requestBody(ChatMessage.class)
            .response(ChatMessage.class, "实际发送的消息")
            .and()
            //
            .webInterface()
            .uri("/sendingBroadcastState")
            .description("获取当前的自动发送广播配置")
            .response(SendingBroadcastSetting.class, "广播配置")
            .and()
            //
            .webInterface()
            .uri("/createSendingBroadcastTask")
            .post()
            .description("创建一个新的自动发送广播配置，会取代原有的")
            .response(SendingBroadcastSetting.class, "广播配置")
            .and()
            //
            .webInterface()
            .uri("/cancleSendingBroadcastTask")
            .post()
            .description("取消当前的自动发送广播配置")
            .and()
            ;
    }
}
