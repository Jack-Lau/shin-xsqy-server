/*
 * Created 2015-10-16 15:23:41
 */
package io.github.azige.mgxy.event;

/**
 * 作为类型限定的事件类型接口，通常应该由枚举类型来实现此接口，或者是单例对象。
 * 但是后者可能会有问题，如果不考虑序列化的情况下也可以使用。
 *
 * @author Azige
 * @param <T> 此事件类型对应的事件对象的类型。
 */
public interface EventType<T extends Event>{

}
