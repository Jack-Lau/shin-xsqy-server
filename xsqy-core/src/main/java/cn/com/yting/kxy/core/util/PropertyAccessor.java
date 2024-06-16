/*
 * Created 2018-9-12 11:27:40
 */
package cn.com.yting.kxy.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class PropertyAccessor<T> {

    private Supplier<T> getter;
    private Consumer<T> setter;
}
