/*
 * Created 2018-6-28 16:09:21
 */
package cn.com.yting.kxy.web.message;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个类，指明它会在 web 消息中使用。
 * {@link WebMessageSchemaHolder} 会为其生成 JSON Schema
 *
 * @author Azige
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebMessageType {

}
