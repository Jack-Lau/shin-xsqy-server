/*
 * Created 2018-7-21 12:38:24
 */
package cn.com.yting.kxy.web.currency;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class CurrencyStack {

    private static final Pattern TEXT_PATTERN = Pattern.compile("(.+):(.+)");

    private long currencyId;
    private long amount;

    @JsonCreator
    public CurrencyStack(
        @JsonProperty("currencyId") long currencyId,
        @JsonProperty("amount") long amount
    ) {
        this.currencyId = currencyId;
        this.amount = amount;
    }

    public String toText() {
        return "" + currencyId + ":" + amount;
    }

    public static CurrencyStack fromText(String text) {
        Matcher matcher = TEXT_PATTERN.matcher(text);
        if (matcher.find()) {
            long currencyId = Long.parseLong(matcher.group(1));
            long amount = Long.parseLong(matcher.group(2));
            return new CurrencyStack(currencyId, amount);
        } else {
            throw new IllegalArgumentException("无法解析的货币堆文本：" + text);
        }
    }

    public static String listToText(List<CurrencyStack> list) {
        return list.stream()
            .map(CurrencyStack::toText)
            .collect(Collectors.joining(","));
    }

    public static List<CurrencyStack> listFromText(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(text.split(","))
                .map(CurrencyStack::fromText)
                .collect(Collectors.toList());
        }
    }
}
