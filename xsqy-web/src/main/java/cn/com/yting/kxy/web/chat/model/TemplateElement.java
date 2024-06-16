/*
 * Created 2018-7-30 16:44:39
 */
package cn.com.yting.kxy.web.chat.model;

import java.util.Map;

import cn.com.yting.kxy.web.message.WebMessageType;

/**
 *
 * @author Azige
 */
@WebMessageType
public class TemplateElement extends ChatElement<TemplateDescription> {

    public TemplateElement(long templateId, Map<String, String> args) {
        this(new TemplateDescription(templateId, args));
    }

    public TemplateElement(TemplateDescription content) {
        super(ChatElementType.TEMPLATE, content);
    }

}
