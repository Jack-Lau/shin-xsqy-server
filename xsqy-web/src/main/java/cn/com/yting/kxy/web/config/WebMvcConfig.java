/*
 * Created 2018-6-25 19:15:36
 */
package cn.com.yting.kxy.web.config;

import java.util.List;

import cn.com.yting.kxy.web.message.WrappedJsonMessageConverter;
import cn.com.yting.kxy.web.proof.ClientProoferFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 *
 * @author Azige
 */
@Configuration
@EnableSpringDataWebSupport
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/hello");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new WrappedJsonMessageConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowCredentials(true)
            .allowedHeaders(
                "Content-Type",
                "X-Auth-Token",
                ClientProoferFilter.HEADER_NAME_RANDOM_VALUE,
                ClientProoferFilter.HEADER_NAME_PROOF_VALUE
            )
            .exposedHeaders(
                "X-Auth-Token"
            );
    }

    @Bean
    public InternalResourceViewResolver viewResolver(){
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        bean.setExposeContextBeansAsAttributes(true);
        return bean;
    }

    @Bean
    public MultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }
}
