/*
 * Created 2018-11-24 17:28:02
 */
package cn.com.yting.kxy.web.apimodel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Azige
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TopicNotification {

    String destination();

    String description();

    Class<?> messageType();
}
