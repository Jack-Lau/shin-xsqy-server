/*
 * Created 2018-8-1 16:44:41
 */
package cn.com.yting.kxy.web.quest.model.objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Value;

/**
 *
 * @author Azige
 */
public abstract class ObjectiveParser<T extends Objective> {

    public static final Pattern CONFIG_SEPERATOR = Pattern.compile(",");

    protected abstract String typeText();

    protected abstract int fieldCount();

    protected abstract Pattern configPattern();

    public List<T> parseConfigs(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        if (text.equals("")) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        for (String part : CONFIG_SEPERATOR.split(text)) {
            list.add(parseConfig(part));
        }
        return list;
    }

    public T parseConfig(String text) {
        Matcher matcher = configPattern().matcher(text.trim());
        if (matcher.matches()) {
            return parseConfigFromMatch(matcher);
        } else {
            throw new IllegalArgumentException("不是有效的" + getClass().getSimpleName() + "能识别的配置文本：" + text);
        }
    }

    protected abstract T parseConfigFromMatch(MatchResult match);

    public ParseResult tryParse(String text) {
        if (text.startsWith(typeText())) {
            String[] fields = text.split(Objective.FIELD_SEPERATOR);
            if (fields.length == Objective.BASE_FIELD_COUNT + fieldCount()) {
                return new ParseResult(true, parseTextFromFields(fields));
            }
        }
        return new ParseResult(false, null);
    }

    public T parseText(String text) {
        String[] fields = text.split(Objective.FIELD_SEPERATOR);
        if (fields.length == Objective.BASE_FIELD_COUNT + fieldCount()) {
            return parseTextFromFields(fields);
        } else {
            throw new IllegalArgumentException("不是有效的" + getClass().getSimpleName() + "能识别的文本：" + text);
        }
    }

    private T parseTextFromFields(String[] fields) {
        String[] exfields = new String[fieldCount()];
        System.arraycopy(fields, Objective.BASE_FIELD_COUNT, exfields, 0, exfields.length);
        T objective = parseTextFromExtendedFields(exfields);
        objective.setCompleted(fields[1].equals("T"));
        return objective;
    }

    protected abstract T parseTextFromExtendedFields(String[] fields);

    @Value
    public static class ParseResult {

        boolean matched;
        Objective result;

    }
}
