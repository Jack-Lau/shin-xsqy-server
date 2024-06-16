/*
 * Created 2018-4-9 15:49:03
 */
package cn.com.yting.kxy.core.resource;

/**
 *
 * @author Azige
 */
public final class ResourceContextHolder{

    private static ResourceContext resourceContext = new AutoScanResourceContext();

    private ResourceContextHolder(){

    }

    public static ResourceContext getResourceContext(){
        return resourceContext;
    }

    public static void setResourceContext(ResourceContext resourceContext){
        ResourceContextHolder.resourceContext = resourceContext;
    }
}
