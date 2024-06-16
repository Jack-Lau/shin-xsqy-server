/*
 * Created 2016-1-8 16:17:17
 */
package cn.com.yting.kxy.battle.generate;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 *
 * @author Azige
 */
public final class PartyStastics {

    // 队长人物等级
    public int LV;
    // 队伍平均等级
    public int ALV;
    // 最高等级
    public int MAXLV;
    // 最高伤害
    public int MAXATK;
    // 最高法伤
    public int MAXSPE;
    // 最高气血
    public int MAXHP;
    // 总人数
    public int TEAM = 1;
    // 强度系数
    public double STR = 0;

    public Bindings toBindings() {
        return Stream.of(getClass().getFields())
                .collect(
                        Collectors.toMap(
                                Field::getName,
                                field -> {
                                    try {
                                        return field.get(this);
                                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                },
                                (a, b) -> a,
                                SimpleBindings::new
                        )
                );
    }
}
