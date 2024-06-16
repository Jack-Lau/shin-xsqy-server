/*
 * Created 2017-7-5 12:07:08
 */
package cn.com.yting.kxy.core.resource;

import java.io.InputStream;
import java.util.Map;

/**
 * 此类型定义了从 XML 中加载资源并使用 Map 来存储资源的资源加载器。
 * 如果资源类类型可以直接映射到 XML 中的数据节点，遵照此模式的实现类仅需要指定
 * 资源名称和资源类型即可。
 *
 * @see MapContainerResourceLoader
 * @see XmlResourceLoader
 * @author Azige
 */
public abstract class XmlMapContainerResourceLoader<T extends Resource> extends MapContainerResourceLoader<T> implements XmlResourceLoader<T> {

    @Override
    public void reload(ResourceContext context, InputStream input) {
        setMap(createMap(context, input));
        afterReload(context);
    }

    protected void afterReload(ResourceContext context) {
    }

    /**
     * 从输入流中构造 Map 对象的方法，默认实现假定此加载器支持的资源类型直接映射到 XML 的数据节点。
     *
     * @param resourceContext
     * @param inputStream
     * @return
     */
    protected Map<Long, T> createMap(ResourceContext resourceContext, InputStream inputStream) {
        return ResourceLoaderUtils.extractDataMap(inputStream, getSupportedClass());
    }
}
