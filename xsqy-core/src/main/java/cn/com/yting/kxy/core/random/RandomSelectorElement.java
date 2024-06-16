/*
 * Created 2018-7-27 10:52:45
 */
package cn.com.yting.kxy.core.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import lombok.Value;

/**
 * 随机抽选元素，描述了一个可以被抽选的结果
 *
 * @author Azige
 */
@Value
public class RandomSelectorElement<T> {

    /**
     * 提供抽选的结果的方法，因为 {@link RandomSelector} 实现了 {@code Supplier<Collection<T>>} 接口，
     * 所以此属性的值可以是一个随机抽选器
     */
    private Supplier<? extends Collection<? extends T>> supplier;
    /**
     * 随机抽选元素的权值，此属性的语义会随 {@link RandomSelectType} 变化
     */
    private double weight;
    /**
     * 抽选的次数，生成结果时根据此值生成复数个结果，最终结果是一个集合
     */
    private int count;

    public Supplier<? extends Collection<? extends T>> getSupplier() {
        return supplier;
    }

    public double getWeight() {
        return weight;
    }

    public Collection<T> createResultCollection() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.addAll(supplier.get());
        }
        return list;
    }
}
