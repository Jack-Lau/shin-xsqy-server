/*
 * Created 2018-7-30 16:38:44
 */
package cn.com.yting.kxy.web.chat.model;

import cn.com.yting.kxy.web.message.WebMessageType;

/**
 *
 * @author Azige
 */
@WebMessageType
public class TextElement extends ChatElement<String> {

    public TextElement(String content) {
        super(ChatElementType.TEXT, content);
    }

}
