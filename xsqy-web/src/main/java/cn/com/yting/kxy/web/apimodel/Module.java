/*
 * Created 2018-6-30 15:22:00
 */
package cn.com.yting.kxy.web.apimodel;

import cn.com.yting.kxy.core.SelfTyped;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.web.apimodel.WebInterface.NestedWebInterfaceBuilder;
import cn.com.yting.kxy.web.apimodel.WebInterface.WebInterfaceBuilder;
import cn.com.yting.kxy.web.apimodel.WebNotification.NestedWebNotificationBuilder;
import cn.com.yting.kxy.web.apimodel.WebNotification.WebNotificationBuilder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Azige
 */
@Data
@Setter(AccessLevel.PROTECTED)
public class Module implements Cloneable {

    private String baseUri;
    private String name;
    private List<Module> submodules;
    private List<WebInterface> webInterfaces;
    private List<WebNotification> webNotifications;

    protected Module() {
    }

    protected void verify() {
        if (baseUri == null) {
            throw new IllegalStateException("模块缺少基准 URI：" + this.toString());
        }
        if (name == null) {
            name = baseUri.replaceFirst("^/", "");
        }
    }

    public Module copy() {
        try {
            return (Module) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ModuleBuilder<?> builder() {
        return new ModuleBuilder();
    }

    public static class ModuleBuilder<SELF extends ModuleBuilder<SELF>> extends SelfTyped<SELF> {

        private final Module valueHolder;
        private final List<ModuleBuilder<?>> submoduleBuidlers = new ArrayList<>();
        private final List<WebInterfaceBuilder<?>> webInterfaceBuilders = new ArrayList<>();
        private final List<WebNotificationBuilder<?>> webNotificationBuilders = new ArrayList<>();

        protected ModuleBuilder() {
            this(new Module());
        }

        protected ModuleBuilder(Module prototype) {
            this.valueHolder = prototype;
        }

        public SELF name(String name) {
            valueHolder.name = name;
            return self();
        }

        public SELF baseUri(String baseUri) {
            valueHolder.baseUri = baseUri;
            return self();
        }

        public NestedmoduleBuilder<SELF> submodule() {
            NestedmoduleBuilder<SELF> submoduleBuilder = new NestedmoduleBuilder<>(self());
            submoduleBuidlers.add(submoduleBuilder);
            return submoduleBuilder;
        }

        public SELF submodule(Module module) {
            ModuleBuilder moduleBuilder = new ModuleBuilder(module);
            submoduleBuidlers.add(moduleBuilder);
            return self();
        }

        public NestedWebInterfaceBuilder<SELF> webInterface() {
            NestedWebInterfaceBuilder<SELF> webInterfaceBuilder = new NestedWebInterfaceBuilder<>(self());
            webInterfaceBuilders.add(webInterfaceBuilder);
            return webInterfaceBuilder;
        }

        public NestedWebNotificationBuilder<SELF> webNotification() {
            NestedWebNotificationBuilder<SELF> webNotificationBuilder = new NestedWebNotificationBuilder<>(self());
            webNotificationBuilders.add(webNotificationBuilder);
            return webNotificationBuilder;
        }

        public Module build() {
            Module module = valueHolder.copy();
            if (module.submodules == null) {
                module.submodules = submoduleBuidlers.stream()
                    .map(ModuleBuilder::build)
                    .collect(Collectors.toList());
            }
            if (module.webInterfaces == null) {
                module.webInterfaces = webInterfaceBuilders.stream()
                    .map(WebInterfaceBuilder::build)
                    .collect(Collectors.toList());
            }
            if (module.webNotifications == null) {
                module.webNotifications = webNotificationBuilders.stream()
                    .map(WebNotificationBuilder::build)
                    .collect(Collectors.toList());
            }
            module.verify();
            return module;
        }
    }

    public static class NestedmoduleBuilder<P> extends ModuleBuilder<NestedmoduleBuilder<P>> {

        private final P parent;

        protected NestedmoduleBuilder(P parent) {
            this.parent = parent;
        }

        public P and() {
            return parent;
        }
    }
}
