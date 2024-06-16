/*
 * Created 2018-10-23 12:43:23
 */
package cn.com.yting.kxy.web.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Azige
 */
@Configuration
public class ChatWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/management/chat", "/management/chat/");
        registry.addViewController("/management/chat/").setViewName("chat/index");
    }

}
