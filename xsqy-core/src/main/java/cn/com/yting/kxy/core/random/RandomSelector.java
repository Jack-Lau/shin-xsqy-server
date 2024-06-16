/*
 * Created 2016-1-6 10:37:23
 */
package cn.com.yting.kxy.core.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 *
 * @author Azige
 */
public class RandomSelector<T> implements Supplier<Collection<T>> {

    private final Collection<RandomSelectorElement<T>> elements;
    private final RandomSelectType type;

    public RandomSelector(Collection<RandomSelectorElement<T>> elements, RandomSelectType type) {
        this.elements = new ArrayList<>(elements);
        this.type = type;
    }

    @Override
    public Collection<T> get() {
        List<T> resultList = new ArrayList<>();
        if (type.equals(RandomSelectType.DEPENDENT)) {
            double weightSum = elements.stream()
                .mapToDouble(RandomSelectorElement::getWeight)
                .sum();
            double rand = RandomProvider.getRandom().nextDouble() * weightSum;
            RandomSelectorElement<T> element = null;
            for (Iterator<RandomSelectorElement<T>> iterator = elements.iterator(); iterator.hasNext() && rand > 0;) {
                element = iterator.next();
                rand -= element.getWeight();
            }
            if (element != null) {
                resultList.addAll(element.createResultCollection());
            }
        } else if (type.equals(RandomSelectType.INDEPENDENT)) {
            Random random = RandomProvider.getRandom();
            elements.stream()
                .filter(element -> random.nextDouble() < element.getWeight())
                .map(RandomSelectorElement::createResultCollection)
                .forEach(resultList::addAll);
        }
        return resultList;
    }

    public T getSingle() {
        Collection<T> results = get();
        if (results.isEmpty()) {
            return null;
        }
        return results.iterator().next();
    }

    public RandomSelectType getType() {
        return type;
    }

    public static <T> RandomSelectorBuilder<T> builder() {
        return new RandomSelectorBuilder<>();
    }
}
