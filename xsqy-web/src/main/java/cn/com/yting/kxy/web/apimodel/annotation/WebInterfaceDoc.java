/*
 * Created 2018-11-19 18:53:21
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
public @interface WebInterfaceDoc {

    String name() default "";

    String description();

    String response();
}
