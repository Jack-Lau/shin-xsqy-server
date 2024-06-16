/*
 * Created 2017-7-15 10:44:36
 */
package cn.com.yting.kxy.core.resource;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 *
 * @author Azige
 */
public class LazyLoadResourceContext implements ResourceContext{

    private ChainedResourceTypeMap<LoaderContainer> loaderContainerMap = new ChainedResourceTypeMap<>();
    private ClassLoader classLoader;

    public LazyLoadResourceContext(Collection<ResourceLoader<?>> loaders){
        this(loaders, Thread.currentThread().getContextClassLoader());
    }

    public LazyLoadResourceContext(Collection<ResourceLoader<?>> loaders, ClassLoader classLoader) {
        this.classLoader = classLoader;
        loaders.forEach(it -> loaderContainerMap.put(it.getSupportedClass(), new LoaderContainer(it)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Resource> ResourceLoader<T> getLoader(Class<T> type) throws NoSuchElementException{
        Collection<LoaderContainer> containers = loaderContainerMap.get(type);
        if (containers.isEmpty()){
            throw new NoSuchElementException("资源加载器不存在，资源类型=" + type);
        }
        containers.stream()
            .filter(it -> !it.initilized)
            .forEach(LoaderContainer::init);
        if (containers.size() == 1){
            return (ResourceLoader<T>)containers.iterator().next().loader;
        }else{
            return new AggregateResourceLoader<>(
                type,
                containers.stream()
                    .map(it -> (ResourceLoader<? extends T>)it.loader)
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ResourceLoader<?>> T getByLoaderType(Class<T> type) throws NoSuchElementException{
        return loaderContainerMap.getAllElements().stream()
            .filter(it -> it.loader.getClass().equals(type))
            .map(lc -> {
                if (!lc.initilized){
                    lc.init();
                }
                return (T)lc.loader;
            })
            .findAny().orElseThrow(() -> new NoSuchElementException("资源加载器不存在，加载器类型=" + type));
    }

    @Override
    public ClassLoader getClassLoader(){
        return classLoader;
    }

    /**
     * 为此类设置新的类加载器并将所有资源加载器设置为未初始化的状态。
     *
     * @param classLoader
     */
    public void reloadAll(ClassLoader classLoader){
        this.classLoader = classLoader;
        loaderContainerMap.getAllElements().forEach(container -> container.setInitilized(false));
    }

    /**
     * 获取所有的记录初始化状态的资源加载器容器。
     * 通常应该只在需要对资源加载器进行管理的地方调用此方法。
     *
     * @return
     */
    public Collection<LoaderContainer> getAllLoaderContainers(){
        return loaderContainerMap.getAllElements();
    }

    /**
     * 用于记录初始化状态的资源加载器容器。
     * 通常不应当在此对象上进行操作，除非需要使用特别的资源对资源加载器进行初始化。
     * 在单独的初始化完成后应调用 {@code setInitilized(true)} 将状态设置为已初始化。
     */
    public class LoaderContainer{

        private boolean initilized = false;
        private ResourceLoader<?> loader;

        private LoaderContainer(ResourceLoader<?> loader){
            this.loader = loader;
        }

        public ResourceLoader<?> getLoader(){
            return loader;
        }

        public boolean isInitilized(){
            return initilized;
        }

        public void setInitilized(boolean initilized){
            this.initilized = initilized;
        }

        private synchronized void init(){
            if (!initilized){
                loader.reload(LazyLoadResourceContext.this);
                initilized = true;
            }
        }
    }
}
