/*
 * Created 2018-8-1 16:41:48
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
public class NullObjective extends Objective {

    public static final String TYPE_TEXT = "NullObjective";
    private static final Pattern CONFIG_PATTERN = Pattern.compile("无_([^,]+)");

    private final String result;

    @Override
    protected String typeText() {
        return TYPE_TEXT;
    }

    @Override
    protected String[] extendedFields() {
        return new String[]{result};
    }

    @Override
    public String toString() {
        return toText();
    }

    public static NullObjectiveParser parser() {
        return new NullObjectiveParser();
    }

    public static class NullObjectiveParser extends ObjectiveParser<NullObjective> {

        @Override
        protected String typeText() {
            return TYPE_TEXT;
        }

        @Override
        protected int fieldCount() {
            return 1;
        }

        @Override
        protected Pattern configPattern() {
            return CONFIG_PATTERN;
        }

        @Override
        protected NullObjective parseConfigFromMatch(MatchResult match) {
            return new NullObjective(match.group(1));
        }

        @Override
        protected NullObjective parseTextFromExtendedFields(String[] fields) {
            return new NullObjective(fields[0]);
        }

    }
}
