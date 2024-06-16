/*
 * Created 2018-7-19 17:17:45
 */
package cn.com.yting.kxy.web.chat.model;

import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
@EqualsAndHashCode
@WebMessageType
@JsonDeserialize(using = ChatElementDeserializer.class)
public class ChatElement<T> {

    private final ChatElementType type;
    private final T content;

    public ChatElement(ChatElementType type, T content) {
        this.type = type;
        this.content = content;
    }

}
