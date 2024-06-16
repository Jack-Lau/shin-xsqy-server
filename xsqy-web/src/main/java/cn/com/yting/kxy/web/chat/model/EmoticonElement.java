/*
 * Created 2018-7-30 16:43:10
 */
package cn.com.yting.kxy.web.chat.model;

import cn.com.yting.kxy.web.message.WebMessageType;

/**
 *
 * @author Azige
 */
@WebMessageType
public class EmoticonElement extends ChatElement<Long> {

    public EmoticonElement(long emoticon) {
        super(ChatElementType.EMOTICON, emoticon);
    }

}
