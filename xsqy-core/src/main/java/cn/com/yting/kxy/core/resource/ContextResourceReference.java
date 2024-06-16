/*
 * Created 2017-7-4 17:37:32
 */
package cn.com.yting.kxy.core.resource;

/**
 *
 * @author Azige
 */
class ContextResourceReference<T extends Resource> extends AbstractResourceReference<T>{

    private final ResourceContext context;

    public ContextResourceReference(ResourceContext context, Class<T> type, long id){
        super(type, id);
        this.context = context;
    }

    @Override
    public boolean exists(){
        return context.getLoader(getType()).exists(getId());
    }

    @Override
    public T get(){
        return context.getLoader(getType()).get(getId());
    }
}
