/*
 * Created 2018-8-6 15:39:02
 */
package cn.com.yting.kxy.web.chat;

import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class ChatMessageComplex {

    private ChatMessage chatMessage;
    private PlayerBaseInfo senderPlayer;
    
}
