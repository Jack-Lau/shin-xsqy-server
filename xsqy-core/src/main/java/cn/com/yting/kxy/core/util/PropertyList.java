/*
 * Created 2018-9-12 11:23:02
 */
package cn.com.yting.kxy.core.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author Azige
 */
public class PropertyList<E> extends AbstractList<E> implements RandomAccess {

    private final PropertyAccessor<E>[] accessors;

    private PropertyList(PropertyAccessor<E>[] accessors, boolean copy) {
        if (copy) {
            this.accessors = Arrays.copyOf(accessors, accessors.length);
        } else {
            this.accessors = accessors;
        }
    }

    @Override
    public E get(int index) {
        return accessors[index].getGetter().get();
    }

    @Override
    public E set(int index, E element) {
        E oldValue = accessors[index].getGetter().get();
        accessors[index].getSetter().accept(element);
        return oldValue;
    }

    @Override
    public int size() {
        return accessors.length;
    }

    public static <E> PropertyListBuilder<E> builder() {
        return new PropertyListBuilder<>();
    }

    public static class PropertyListBuilder<E> {

        private final List<PropertyAccessor<E>> accessors = new ArrayList<>();

        public PropertyListBuilder<E> add(PropertyAccessor<E> accessor) {
            accessors.add(accessor);
            return this;
        }

        public PropertyListBuilder<E> add(Supplier<E> getter, Consumer<E> setter) {
            return add(new PropertyAccessor<>(getter, setter));
        }

        @SuppressWarnings("unchecked")
        public PropertyList<E> build() {
            return new PropertyList<>(accessors.toArray(new PropertyAccessor[0]), false);
        }
    }
}
