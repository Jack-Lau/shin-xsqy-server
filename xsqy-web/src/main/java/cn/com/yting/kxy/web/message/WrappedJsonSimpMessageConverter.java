/*
 * Created 2018-7-13 17:38:49
 */
package cn.com.yting.kxy.web.message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 *
 * @author Azige
 */
public class WrappedJsonSimpMessageConverter extends MappingJackson2MessageConverter {

    public WrappedJsonSimpMessageConverter() {
        getObjectMapper().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        Class<?> clz = payload.getClass();
        if (!clz.equals(WebMessageWrapper.class)) {
            JsonNode jsonNode = getObjectMapper().valueToTree(payload);
            String messageContentType;
            if (jsonNode.isObject()) {
                messageContentType = clz.getSimpleName();
            } else {
                messageContentType = jsonNode.getNodeType().toString().toLowerCase();
            }
            payload = WebMessageWrapper.ok(messageContentType, jsonNode);
        }
        return super.convertToInternal(payload, headers, conversionHint);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

}
