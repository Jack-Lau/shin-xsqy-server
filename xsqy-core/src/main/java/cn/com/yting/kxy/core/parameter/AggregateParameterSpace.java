/*
 * Created 2015-11-16 16:34:15
 */
package cn.com.yting.kxy.core.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聚合参数空间是用来组合其它参数空间的参数空间，其所包含的参数空间称为子空间。
 * 此类对象对于参数基元的映射是所有子空间的映射{@link ParameterBase#plus 相加}的结果。 此类使用
 * {@link ArrayList} 作为子空间容器。
 *
 * @author Azige
 */
public class AggregateParameterSpace implements ParameterSpace {

    private Collection<ParameterSpace> subspaces;

    /**
     * 构建一个空的聚合空间。
     */
    public AggregateParameterSpace() {
        this(Collections.emptyList());
    }

    /**
     * 以子空间集合构建一个聚合空间。
     *
     * @param subspaces 子空间集合
     * @throws NullPointerException 如果子空间集合集合中包含 null
     */
    public AggregateParameterSpace(Collection<ParameterSpace> subspaces) {
        if (subspaces.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("子空间集合中包含null");
        }
        this.subspaces = new ArrayList<>(subspaces);
    }

    /**
     * 以子空间数组构建一个聚合空间。
     *
     * @param subspaces 子空间数组
     * @throws NullPointerException 如果子空间集合集合中包含 null
     */
    public AggregateParameterSpace(ParameterSpace... subspaces) {
        this(Arrays.asList(subspaces));
    }

    /**
     * 获得此对象的子空间集合。对返回对象的修改会影响此对象。
     *
     * @return 此对象的子空间集合
     */
    public Collection<ParameterSpace> getSubspaces() {
        return subspaces;
    }

    @Override
    public ParameterBase getParameterBase(String name) {
        return subspaces.stream()
                .filter(Objects::nonNull)
                .map(s -> s.getParameterBase(name))
                .filter(Objects::nonNull)
                .reduce(ParameterBase.ZERO, ParameterBase::plus);
    }

    @Override
    public Map<String, ParameterBase> toMap() {
        return subspaces.stream()
                .filter(Objects::nonNull)
                .flatMap(ps -> ps.toMap().entrySet().stream())
                .collect(
                        Collectors.groupingBy(Entry::getKey,
                                Collectors.reducing(ParameterBase.ZERO, Entry::getValue, ParameterBase::plus)
                        )
                );
    }
}
