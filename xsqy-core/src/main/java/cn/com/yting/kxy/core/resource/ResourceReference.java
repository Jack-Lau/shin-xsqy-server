/*
 * Created 2017-5-9 12:38:57
 */
package cn.com.yting.kxy.core.resource;

/**
 * 一个间接的资源引用。
 * 用来解决资源加载器初始化时需要引用未加载的资源的问题以及
 * 确保资源加载器在重新加载后原本的引用不需要更换即可访问新的资源。
 *
 * @author Azige
 */
public interface ResourceReference<T extends Resource>{

    long getId();

    Class<T> getType();

    boolean exists();

    T get();

}
