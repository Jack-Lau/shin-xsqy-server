/*
 * Created 2018-7-13 18:19:34
 */
package cn.com.yting.kxy.web.apimodel;

import cn.com.yting.kxy.core.SelfTyped;
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
public class WebNotification implements Cloneable {

    private String destination;
    private String description;
    private String messageJsonType;

    public WebNotification copy() {
        try {
            return (WebNotification) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class WebNotificationBuilder<SELF extends WebNotificationBuilder<SELF>> extends SelfTyped<SELF> {

        private final WebNotification valueHolder = new WebNotification();

        protected WebNotificationBuilder() {

        }

        public SELF destination(String destination) {
            valueHolder.destination = destination;
            return self();
        }

        public SELF queue(String destination) {
            return destination("/user/queue" + destination);
        }

        public SELF topic(String destination) {
            return destination("/topic" + destination);
        }

        public SELF description(String description) {
            valueHolder.description = description;
            return self();
        }

        public SELF messageType(Class<?> mappedJavaType) {
            valueHolder.messageJsonType = mappedJavaType.getSimpleName();
            return self();
        }

        public WebNotification build() {
            return valueHolder.copy();
        }
    }

    public static class NestedWebNotificationBuilder<P> extends WebNotificationBuilder<NestedWebNotificationBuilder<P>> {

        private final P parent;

        protected NestedWebNotificationBuilder(P parent) {
            this.parent = parent;
        }

        public P and() {
            return parent;
        }
    }
}
