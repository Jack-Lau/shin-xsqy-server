/*
 * Created 2018-7-11 16:11:18
 */
package cn.com.yting.kxy.web.config;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import cn.com.yting.kxy.web.message.WrappedJsonSimpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.util.Assert;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebMvcStompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

/**
 *
 * @author Azige
 */
@EnableWebSocketMessageBroker
@Configuration
public class WebsocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new WrappedJsonSimpMessageConverter());
        return false;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .nullDestMatcher().authenticated()
            .simpSubscribeDestMatchers("/user/queue/**", "/topic/**").authenticated()
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.configureBrokerChannel().taskExecutor();
        registry.enableSimpleBroker().setTaskScheduler(taskScheduler);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        if (registry instanceof WebMvcStompEndpointRegistry) {
            try {
                Field field = WebMvcStompEndpointRegistry.class.getDeclaredField("stompHandler");
                field.setAccessible(true);
                StompSubProtocolHandler stompHandler = (StompSubProtocolHandler) field.get(registry);
                stompHandler.setEncoder(new StompEncoder() {
                    public byte[] encode(Map<String, Object> headers, byte[] payload) {
                        byte[] bytes = super.encode(headers, payload);
                        // 不修改心跳的 LF
                        if (bytes.length > 1) {
                            bytes[bytes.length - 1] = ModifiedStompDecoder.END_OF_MESSAGE;
                        }
                        return bytes;
                    }
                });
                stompHandler.setDecoder(new ModifiedStompDecoder());
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
        registry.addEndpoint("/websocket").setAllowedOrigins("*");
    }
}
