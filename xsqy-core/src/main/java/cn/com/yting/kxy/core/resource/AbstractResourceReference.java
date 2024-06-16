/*
 * Created 2017-5-8 11:45:01
 */
package cn.com.yting.kxy.core.resource;

/**
 *
 * @author Azige
 * @param <T> 引用的资源的类型
 */
public abstract class AbstractResourceReference<T extends Resource> implements ResourceReference<T>{

    private Class<T> type;
    private long id;

    protected AbstractResourceReference(Class<T> type, long id){
        this.type = type;
        this.id = id;
    }

    @Override
    public Class<T> getType(){
        return type;
    }

    @Override
    public long getId(){
        return id;
    }

}
