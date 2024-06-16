/*
 * Created 2018-7-27 10:52:58
 */
package cn.com.yting.kxy.core.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Azige
 */
public class RandomSelectorBuilder<T> {

    private List<RandomSelectorElement<T>> elements = new ArrayList<>();

    public RandomSelectorBuilder<T> add(Supplier<? extends Collection<? extends T>> supplier, double weight) {
        return add(supplier, weight, 1);
    }

    public RandomSelectorBuilder<T> add(T single, double weight) {
        Collection<T> collection = Collections.singleton(single);
        return add(() -> collection, weight, 1);
    }

    public RandomSelectorBuilder<T> add(T single, double weight, int count) {
        Collection<T> collection = Collections.singleton(single);
        return add(() -> collection, weight, count);
    }

    public RandomSelectorBuilder<T> add(Supplier<? extends Collection<? extends T>> supplier, double weight, int count) {
        elements.add(new RandomSelectorElement<>(supplier, weight, count));
        return this;
    }

    public RandomSelector<T> build(RandomSelectType type) {
        return new RandomSelector<>(elements, type);
    }

}
