/*
 * Created 2018-11-14 12:12:05
 */
package cn.com.yting.kxy.web.auction;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Azige
 */
@Configuration
public class AuctionWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/management/auction", "/management/auction/");
        registry.addViewController("/management/auction/").setViewName("auction/index");
    }
}
