/*
 * Created 2017-5-8 16:10:14
 */
package cn.com.yting.kxy.core.resource;

import java.util.NoSuchElementException;

/**
 * 描述一个资源环境，环境中的资源若有需要应当引用同一环境中的其它资源。
 *
 * @author Azige
 */
public interface ResourceContext{

    <T extends Resource> ResourceLoader<T> getLoader(Class<T> type) throws NoSuchElementException;

    <T extends ResourceLoader<?>> T getByLoaderType(Class<T> type) throws NoSuchElementException;

    /**
     * 获得此资源环境的类加载器。
     * 让资源加载器可以使用特定的类加载器来读取资源。
     *
     * @return
     */
    default ClassLoader getClassLoader(){
        return getClass().getClassLoader();
    }

    default <T extends Resource> ResourceReference<T> createReference(Class<T> type, long id){
        // 资源引用不能直接使用资源加载器来构造，否则在某些资源加载器初始化的时候会导致死递归
        return new ContextResourceReference<>(this, type, id);
    }
}
