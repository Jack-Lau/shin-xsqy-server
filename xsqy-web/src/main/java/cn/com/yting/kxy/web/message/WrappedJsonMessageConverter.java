/*
 * Created 2018-6-29 11:54:02
 */
package cn.com.yting.kxy.web.message;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 *
 * @author Azige
 */
public class WrappedJsonMessageConverter implements HttpMessageConverter<Object> {

    private final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return jsonConverter.canRead(clazz, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return jsonConverter.canWrite(clazz, mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return jsonConverter.getSupportedMediaTypes();
    }

    @Override
    public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return jsonConverter.read(clazz, inputMessage);
    }

    @Override
    public void write(Object t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Class<?> clz = t.getClass();

        // 将 Optional 解构为实际的消息或替换成空消息
        if (clz.equals(Optional.class)) {
            Optional<?> optional = (Optional<?>) t;
            if (optional.isPresent()) {
                t = optional.get();
            } else {
                t = WebMessageWrapper.ok();
            }
            clz = t.getClass();
        }

        // 从 RawWebMessageWrapper 中取出原始消息
        if (clz.equals(RawWebMessageWrapper.class)) {
            RawWebMessageWrapper rawWebMessageWrapper = (RawWebMessageWrapper) t;
            t = rawWebMessageWrapper.getMessage();
        } else if (!clz.equals(WebMessageWrapper.class)) {
            // 将其它类型的消息用 WebMessageWrapper 包装
            JsonNode jsonNode = objectMapper.valueToTree(t);
            String messageContentType;
            if (jsonNode.isObject()) {
                messageContentType = clz.getSimpleName();
            } else {
                messageContentType = jsonNode.getNodeType().toString().toLowerCase();
            }
            t = WebMessageWrapper.ok(messageContentType, jsonNode);
        }
        jsonConverter.write(t, contentType, outputMessage);
    }
}
