/*
 * Created 2018-6-29 15:47:31
 */
package cn.com.yting.kxy.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author Azige
 */
@Configuration
@PropertySource(value = "classpath:datasource.properties", ignoreResourceNotFound = true)
public class DataConfig {

}
