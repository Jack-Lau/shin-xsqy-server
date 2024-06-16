/*
 * Created 2017-7-5 11:53:40
 */
package cn.com.yting.kxy.core.resource;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 此类提供一部分以 Map 来管理资源的基础代码，实现类只需要在 reload 中调用
 * {@link #setMap(java.util.Map)} 来指定资源。
 *
 * @author Azige
 */
public abstract class MapContainerResourceLoader<T extends Resource> implements ResourceLoader<T>{

    private Map<Long, T> map;

    @Override
    public boolean exists(long id){
        return map.containsKey(id);
    }

    @Override
    public T get(long id) throws NoSuchElementException{
        T resource = map.get(id);
        if (resource == null){
            throw new NoSuchElementException("资源不存在，loader=" + getClass() + ", id=" + id);
        }
        return resource;
    }

    @Override
    public Map<Long, T> getAll(){
        return Collections.unmodifiableMap(map);
    }

    protected Map<Long, T> getMap(){
        return map;
    }

    protected void setMap(Map<Long, T> map){
        this.map = map;
    }

}
