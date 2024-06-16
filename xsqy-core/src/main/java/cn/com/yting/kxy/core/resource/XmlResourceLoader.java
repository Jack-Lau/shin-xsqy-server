/*
 * Created 2017-7-4 17:42:23
 */
package cn.com.yting.kxy.core.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import org.slf4j.LoggerFactory;

/**
 * 此类型定义了从 XML 中读取资源的资源加载器。
 * 提供若干便捷方法，通过 {@link #getDefaultResourceName()} 返回一个资源名
 * 以便从默认的类路径中加载 XML 源。
 *
 * @author Azige
 */
public interface XmlResourceLoader<T extends Resource> extends ResourceLoader<T>{

    String getDefaultResourceName();

    @Override
    default void reload(ResourceContext context){
        reload(context, context.getClassLoader(), getDefaultResourceName());
    }

    default void reload(ResourceContext context, ClassLoader classLoader){
        reload(context, classLoader, getDefaultResourceName());
    }

    default void reload(ResourceContext context, ClassLoader classLoader, String resourceName){
        try (InputStream input = classLoader.getResourceAsStream(resourceName)){
            Objects.requireNonNull(input, "资源不存在，resourceName=" + resourceName);
            reload(context, input);
        }catch (IOException ex){
            LoggerFactory.getLogger(getClass()).error("发生异常", ex);
        }
    }

    default void reload(ResourceContext context, String xmlText){
        reload(context, new ByteArrayInputStream(xmlText.getBytes(Charset.forName("UTF-8"))));
    }

    void reload(ResourceContext context, InputStream input);
}
