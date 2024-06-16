/*
 * Created 2017-5-8 11:38:44
 */
package cn.com.yting.kxy.core.resource;

/**
 * mgxy 中对资源的定义。 所有资源都拥有一个全局唯一的 id。
 *
 * @author Azige
 */
public interface Resource {

    /**
     * 如果一种资源不支持 id，则 {@link #getId()} 返回此值
     */
    long ID_UNSUPPORTED = -1;

    long getId();

}
