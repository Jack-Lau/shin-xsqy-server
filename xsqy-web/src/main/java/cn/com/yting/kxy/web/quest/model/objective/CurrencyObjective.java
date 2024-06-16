/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.quest.model.objective;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
@AllArgsConstructor
public class CurrencyObjective extends Objective {

    public static final String TYPE_TEXT = "CurrencyObjective";
    private static final Pattern CONFIG_PATTERN = Pattern.compile("(\\d+)_(\\d+)_([^,]+)");

    private final long currencyId;
    private final long currencyAmount;
    private final String result;

    @Override
    protected String typeText() {
        return TYPE_TEXT;
    }

    @Override
    protected String[] extendedFields() {
        return new String[]{
            String.valueOf(currencyId),
            String.valueOf(currencyAmount),
            result
        };
    }

    @Override
    public String toString() {
        return toText();
    }

    public static CurrencyObjectiveParser parser() {
        return new CurrencyObjectiveParser();
    }

    public static class CurrencyObjectiveParser extends ObjectiveParser<CurrencyObjective> {

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
        protected CurrencyObjective parseConfigFromMatch(MatchResult match) {
            long currencyId = Long.parseLong(match.group(1));
            long currencyAmount = Long.parseLong(match.group(2));
            String result = match.group(3);
            return new CurrencyObjective(currencyId, currencyAmount, result);
        }

        @Override
        protected CurrencyObjective parseTextFromExtendedFields(String[] fields) {
            long currencyId = Long.parseLong(fields[0]);
            long currencyAmount = Long.parseLong(fields[1]);
            String result = fields[2];
            return new CurrencyObjective(currencyId, currencyAmount, result);
        }

    }

}
