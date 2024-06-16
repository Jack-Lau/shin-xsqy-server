/*
 * Created 2015-10-23 10:37:57
 */
package cn.com.yting.kxy.battle.skill;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import java.util.ArrayList;

/**
 *
 * @author Azige
 */
public class MultiTargetSelectors {

    public static List<Unit> 默认选择策略(Unit main, List<Unit> optionalTargets) {
        return optionalTargets.stream()
                .sorted(Comparator.comparing(Unit::getSpeed).reversed())
                .collect(Collectors.toList());
    }

    public static List<Unit> 按hp比例从低到高(Unit main, List<Unit> optionalTargets) {
        return optionalTargets.stream()
                .sorted((a, b) -> {
                    return a.getHp().getRate() > b.getHp().getRate() ? 1 : -1;
                })
                .collect(Collectors.toList());
    }

    public static List<Unit> 多重随机选择(Unit main, List<Unit> optionalTargets, int maxTargetCount) {
        // TODO
        return optionalTargets;
    }

    public static List<Unit> 优先选择可封印(Unit main, List<Unit> optionalTargets) {
        List<Unit> ot = MultiTargetSelectors.按hp比例从高到低(main, optionalTargets);
        List<Unit> result = new ArrayList<>();
        ot.stream()
                .filter((u) -> (!u.isHpZero() && !u.hasBuff("六脉血逆") && !u.hasBuff("万蛊噬心") && !u.hasBuff("封魂咒") && u.getParameter(ParameterNameConstants.免疫封印).getValue() <= 0))
                .forEachOrdered((u) -> {
                    result.add(u);
                });
        ot.stream()
                .filter((u) -> (!result.contains(u)))
                .forEachOrdered((u) -> {
                    result.add(u);
                });
        return result;
    }

    public static List<Unit> 按hp比例从高到低(Unit main, List<Unit> optionalTargets) {
        return optionalTargets.stream()
                .sorted((a, b) -> {
                    return a.getHp().getRate() > b.getHp().getRate() ? -1 : 1;
                })
                .collect(Collectors.toList());
    }

}
