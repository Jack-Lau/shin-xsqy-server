/*
 * Created 2018-7-30 17:19:44
 */
package cn.com.yting.kxy.web.chat.model;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Azige
 */
public class ChatElementDeserializer extends JsonDeserializer<ChatElement<?>> {

    @Override
    public ChatElement<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.readValueAsTree();
        String type = node.get("type").asText();
        if (Objects.equals(type, ChatElementType.TEXT.name())) {
            return new TextElement(node.get("content").asText());
        } else if (Objects.equals(type, ChatElementType.EMOTICON.name())) {
            return new EmoticonElement(node.get("content").asInt());
        } else if (Objects.equals(type, ChatElementType.TEMPLATE.name())) {
            return new TemplateElement(p.getCodec().treeToValue(node.get("content"), TemplateDescription.class));
        }
        throw new IllegalStateException("无法解析的消息元素类型：" + type);
    }

}
