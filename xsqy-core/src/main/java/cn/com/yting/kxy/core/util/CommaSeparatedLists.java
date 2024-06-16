/*
 * Created 2018-9-26 12:28:49
 */
package cn.com.yting.kxy.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Azige
 */
public class CommaSeparatedLists {

    public static <T> List<T> fromText(String text, Function<String, T> mapper) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(text.split(","))
                .map(mapper)
                .collect(Collectors.toList());
        }
    }

    public static String toText(List<?> list) {
        return list.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    }
}
