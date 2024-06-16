/*
 * Created 2018-7-7 12:56:20
 */
package cn.com.yting.kxy.core.scheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Azige
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterScheduledTask {

    String name() default "";

    String fullName() default "";

    String cronExpression();

    boolean executeIfNew() default false;
}
