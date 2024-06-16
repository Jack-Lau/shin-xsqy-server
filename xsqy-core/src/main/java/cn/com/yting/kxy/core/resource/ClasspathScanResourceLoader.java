/*
 * Created 2018-8-13 18:34:09
 */
package cn.com.yting.kxy.core.resource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * 在类路径中搜索资源的资源加载器。会在类路径中搜索所有支持的资源类型的派生类型，
 * 然后使用无参构造方法创建一个对象作为资源对象
 *
 * @author Azige
 * @param <T> 资源类型
 */
public abstract class ClasspathScanResourceLoader<T extends Resource> extends MapContainerResourceLoader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathScanResourceLoader.class);

    @Override
    public final void reload(ResourceContext context) {
        reload(context, context.getClassLoader());
    }

    public final void reload(ResourceContext context, ClassLoader classLoader) {
        reload(context, classLoader, defaultBasePackage());
    }

    public final void reload(ResourceContext context, ClassLoader classLoader, String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(getSupportedClass()));
        scanner.addExcludeFilter(new AnnotationTypeFilter(NotResource.class));
        List<T> resources = scanner.findCandidateComponents(basePackage).stream()
            .map(definition -> {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends T> resourceClass = (Class<? extends T>) Class.forName(definition.getBeanClassName());
                    if (resourceClass == getSupportedClass()) {
                        return null;
                    }
                    T resourceObject = resourceClass.newInstance();
                    return resourceObject;
                } catch (ClassNotFoundException ex) {
                    throw new IllegalStateException(ex);
                } catch (InstantiationException | IllegalAccessException ex) {
                    LOG.error("生成类 {} 的对象失败，可能没有公开的无参构造方法", definition.getBeanClassName(), ex);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        setMap(ResourceLoaderUtils.convertListToMap(resources));
        afterReload(context);
    }

    protected void afterReload(ResourceContext context) {
    }

    protected String defaultBasePackage() {
        return "cn.com.yting.kxy";
    }
}
