/*
 * Created 2016-7-3 16:44:01
 */
package cn.com.yting.kxy.web.quest.model.objective;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.com.yting.kxy.web.quest.model.objective.ObjectiveParser.ParseResult;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Azige
 */
public abstract class Objective implements Cloneable {

    public static final String FIELD_SEPERATOR = "_";
    /**
     * 基础的文本类型字段数量 [0]: 类型文本 [1]: 完成与否
     */
    public static final int BASE_FIELD_COUNT = 2;

    private static final List<ObjectiveParser<?>> textParsers = ImmutableList.<ObjectiveParser<?>>builder()
            .add(NullObjective.parser())
            .add(CurrencyObjective.parser())
            .add(BattleObjective.parser())
            .add(LevelObjective.parser())
            .build();

    @Getter
    @Setter
    private boolean completed = false;

    @SuppressWarnings("unchecked")
    public <S extends Objective> S copy() {
        try {
            return (S) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected abstract String typeText();

    protected abstract String[] extendedFields();

    public final String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append(typeText())
                .append(FIELD_SEPERATOR).append(isCompleted() ? "T" : "F");
        for (String field : extendedFields()) {
            sb.append(FIELD_SEPERATOR).append(field);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return toText().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Objective other = (Objective) obj;
        return Objects.equals(this.toText(), other.toText());
    }

    public static Objective fromText(String text) {
        for (ObjectiveParser<?> parser : textParsers) {
            ParseResult result = parser.tryParse(text);
            if (result.isMatched()) {
                return result.getResult();
            }
        }
        throw new IllegalArgumentException("无法解析的任务获得结果文本：" + text);
    }

    public static List<Objective> fromConfigText(String text) {
        return Arrays.stream(ObjectiveParser.CONFIG_SEPERATOR.split(text))
                .map(Objective::fromSingleConfigText)
                .collect(Collectors.toList());
    }

    private static Objective fromSingleConfigText(String text) {
        for (ObjectiveParser<?> parser : textParsers) {
            if (parser.configPattern().matcher(text).matches()) {
                return parser.parseConfig(text);
            }
        }
        throw new IllegalArgumentException("无法解析的任务获得结果配置：" + text);
    }
}
