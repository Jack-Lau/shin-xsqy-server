/*
 * Created 2018-11-19 15:44:33
 */
package cn.com.yting.kxy.web.apimodel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.com.yting.kxy.web.apimodel.ApiScanner;

/**
 * 标注在一个 Controller 上以表示该类型需要生成 API 模型以及提供相关的描述。
 * 此标注用于描述模块信息，{@link WebInterfaceDoc} 用于描述接口信息，
 * {@link ParamDoc} 用于描述接口的参数的信息
 *
 * @author Azige
 * @see ApiScanner
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleDoc {

    String moduleName();
}
