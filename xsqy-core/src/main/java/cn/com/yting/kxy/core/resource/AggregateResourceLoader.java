/*
 * Created 2017-7-11 17:59:44
 */
package cn.com.yting.kxy.core.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Azige
 */
public class AggregateResourceLoader<T extends Resource> implements ResourceLoader<T>{

    private Class<T> supportedClass;
    private List<ResourceLoader<? extends T>> subLoaders;

    public AggregateResourceLoader(Class<T> supportedClass){
        this(supportedClass, new ArrayList<>());
    }

    public AggregateResourceLoader(Class<T> supportedClass, List<ResourceLoader<? extends T>> subLoaders){
        this.supportedClass = supportedClass;
        this.subLoaders = subLoaders;
    }

    @Override
    public boolean exists(long id){
        return subLoaders.stream().anyMatch(it -> it.exists(id));
    }

    @Override
    public T get(long id) throws NoSuchElementException{
        return subLoaders.stream()
            .filter(it -> it.exists(id))
            .findFirst().orElseThrow(() -> new NoSuchElementException("资源不存在，支持的类型=" + getSupportedClasses() + ", id=" + id))
            .get(id);
    }

    @Override
    public Map<Long, T> getAll(){
        return subLoaders.stream()
            .map(ResourceLoader::getAll)
            .collect(HashMap::new, Map::putAll, Map::putAll);
    }

    @Override
    public void reload(ResourceContext context){
        subLoaders.forEach(it -> it.reload(context));
    }

    /**
     * 获取此聚合资源加载器的子加载器集合。
     * 对返回的集合的改变会反映到此对象上。
     *
     * @return
     */
    public List<ResourceLoader<? extends T>> getSubLoaders(){
        return subLoaders;
    }

    @Override
    public Class<T> getSupportedClass(){
        return supportedClass;
    }

    public Set<Class<? extends Resource>> getSupportedClasses(){
        return subLoaders.stream()
            .map(ResourceLoader::getSupportedClass)
            .collect(Collectors.toSet());
    }
}
