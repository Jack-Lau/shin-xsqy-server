/*
 * Created 2018-6-23 18:27:35
 */
package cn.com.yting.kxy.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author Azige
 */
@SpringBootApplication
@ComponentScan("cn.com.yting.kxy")
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
