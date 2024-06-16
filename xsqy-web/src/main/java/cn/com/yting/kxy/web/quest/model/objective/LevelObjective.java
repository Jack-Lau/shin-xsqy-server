/*
 * Created 2018-8-1 17:07:06
 */
package cn.com.yting.kxy.web.quest.model.objective;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
@AllArgsConstructor
public class LevelObjective extends Objective {

    public static final String TYPE_TEXT = "LevelObjective";
    private static final Pattern CONFIG_PATTERN = Pattern.compile("(\\d+)_([^,]+)");

    private final int requiredLevel;
    private final String result;

    @Override
    protected String typeText() {
        return TYPE_TEXT;
    }

    @Override
    protected String[] extendedFields() {
        return new String[]{
            String.valueOf(requiredLevel),
            result
        };
    }

    @Override
    public String toString() {
        return toText();
    }

    public static LevelObjectiveParser parser() {
        return new LevelObjectiveParser();
    }

    public static class LevelObjectiveParser extends ObjectiveParser<LevelObjective> {

        @Override
        protected String typeText() {
            return TYPE_TEXT;
        }

        @Override
        protected int fieldCount() {
            return 2;
        }

        @Override
        protected Pattern configPattern() {
            return CONFIG_PATTERN;
        }

        @Override
        protected LevelObjective parseConfigFromMatch(MatchResult match) {
            int requiredLevel = Integer.parseInt(match.group(1));
            String result = match.group(2);
            return new LevelObjective(requiredLevel, result);
        }

        @Override
        protected LevelObjective parseTextFromExtendedFields(String[] fields) {
            int requiredLevel = Integer.parseInt(fields[0]);
            String result = fields[1];
            return new LevelObjective(requiredLevel, result);
        }

    }
}
