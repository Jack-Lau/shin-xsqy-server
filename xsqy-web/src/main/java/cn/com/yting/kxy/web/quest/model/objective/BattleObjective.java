/*
 * Created 2016-6-14 16:36:54
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
public class BattleObjective extends Objective {

    private static final String TYPE_TEXT = "BattleObjective";
    private static final Pattern CONFIG_PATTERN = Pattern.compile("(\\d+)_([^_]+)_([^,]+)");

    private final long battleDescriptorId;
    private final String wonResult;
    private final String lostResult;

    @Override
    protected String typeText() {
        return TYPE_TEXT;
    }

    @Override
    protected String[] extendedFields() {
        return new String[]{
            String.valueOf(battleDescriptorId),
            wonResult,
            lostResult
        };
    }

    @Override
    public String toString() {
        return toText();
    }

    public static BattleObjectiveParser parser() {
        return new BattleObjectiveParser();
    }

    public static class BattleObjectiveParser extends ObjectiveParser<BattleObjective> {

        @Override
        protected String typeText() {
            return TYPE_TEXT;
        }

        @Override
        protected int fieldCount() {
            return 3;
        }

        @Override
        protected Pattern configPattern() {
            return CONFIG_PATTERN;
        }

        @Override
        protected BattleObjective parseConfigFromMatch(MatchResult match) {
            long battleId = Long.parseLong(match.group(1));
            String wonResult = match.group(2);
            String lostResult = match.group(3);
            return new BattleObjective(battleId, wonResult, lostResult);
        }

        @Override
        protected BattleObjective parseTextFromExtendedFields(String[] fields) {
            long battleId = Long.parseLong(fields[0]);
            String wonResult = fields[1];
            String lostResult = fields[2];
            BattleObjective battleObjective = new BattleObjective(battleId, wonResult, lostResult);
            return battleObjective;
        }
    }
}
