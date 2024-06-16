/*
 * Created 2018-9-12 12:30:25
 */
package cn.com.yting.kxy.core.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在一个资源类型上表示此类型不应当被 {@link ClasspathScanResourceLoader} 加载
 *
 * @author Azige
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotResource {

}
