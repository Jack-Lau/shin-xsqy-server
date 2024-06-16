/*
 * Created 2017-7-15 15:33:09
 */
package cn.com.yting.kxy.core.resource;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 一个用于资源类型的 类型-对象 的多重映射。
 * 任何类型的实现了 {@link Resource} 接口的超类型都能用来检索该类型的映射，
 * 即对于任何已存在的映射 A -> B，存在 S -> B，其中 S 是 A 的的超类型，S 实现了 Resource 接口。
 *
 * @author Azige
 */
public class ChainedResourceTypeMap<T>{

    private Multimap<Class<?>, T> map = HashMultimap.create();

    public void put(Class<?> type, T element){
        for (; type != null && Resource.class.isAssignableFrom(type); type = type.getSuperclass()){
            map.put(type, element);
        }
    }

    /**
     * 检索一个映射关系对应的对象。
     *
     * @param type
     * @return
     */
    public Collection<T> get(Class<?> type){
        return map.get(type);
    }

    /**
     * 获取所有映射关系对应的对象。
     *
     * @return
     */
    public Collection<T> getAllElements(){
        return new HashSet<>(map.values());
    }
}
