/*
 * Created 2017-7-3 18:25:14
 */
package cn.com.yting.kxy.core.resource;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 用于检索资源的资源加载器。
 *
 * @author Azige
 * @param <T> 资源的类型
 */
public interface ResourceLoader<T extends Resource>{

    boolean exists(long id);

    T get(long id) throws NoSuchElementException;

    Map<Long, T> getAll();

    void reload(ResourceContext context);

    Class<T> getSupportedClass();
}
