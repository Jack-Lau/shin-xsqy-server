/*
 * Created 2017-7-4 16:58:22
 */
package cn.com.yting.kxy.core.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * 自动扫描类路径中存在的资源加载器的资源环境。
 *
 * @author Azige
 */
public class AutoScanResourceContext extends LazyLoadResourceContext {

    public static final String DEFAULT_SCAN_PACKAGE = "cn.com.yting";

    private static final Logger LOG = LoggerFactory.getLogger(AutoScanResourceContext.class);
    private static final String SELF_PACKAGE = AutoScanResourceContext.class.getPackage().getName();

    public AutoScanResourceContext() {
        this(DEFAULT_SCAN_PACKAGE);
    }

    public AutoScanResourceContext(String... basePackages) {
        super(findLoaders(basePackages));
    }

    public AutoScanResourceContext(ClassLoader classLoader) {
        super(findLoaders(DEFAULT_SCAN_PACKAGE), classLoader);
    }

    private static Collection<ResourceLoader<?>> findLoaders(String... basePackages) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ResourceLoader.class));
        List<ResourceLoader<?>> loaders = new ArrayList<>();
        for (String basePackage : basePackages) {
            scanner.findCandidateComponents(basePackage).stream()
                .map(beanDefinition -> {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends ResourceLoader> type = (Class<? extends ResourceLoader>) Class.forName(beanDefinition.getBeanClassName());
                        return type;
                    } catch (ClassNotFoundException ex) {
                        // Impossiable
                        throw new RuntimeException(ex);
                    }
                })
                .map(type -> {
                    if (type.getPackage().getName().equals(SELF_PACKAGE)) {
                        LOG.debug("{} 是 {} 中的类型，已跳过", type, SELF_PACKAGE);
                        return null;
                    }
                    if (type.isInterface() || (type.getModifiers() & Modifier.ABSTRACT) != 0) {
                        LOG.debug("{} 是接口或抽象类，已跳过构造", type);
                        return null;
                    }
                    try {
                        Constructor<? extends ResourceLoader> constructor = type.getConstructor();
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                        LOG.error("类 {} 无法实例化，可能没有无参构造方法", type);
                        LOG.debug("类对象构造异常", ex);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(loader -> {
                    LOG.debug("已生成类 {} 的实例", loader.getClass());
                    loaders.add(loader);
                });
        }
        return loaders;
    }
}
