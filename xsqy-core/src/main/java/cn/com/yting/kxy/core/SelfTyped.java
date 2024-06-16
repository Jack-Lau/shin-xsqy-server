/*
 * Created 2018-6-30 16:28:39
 */
package cn.com.yting.kxy.core;

/**
 * 参数化自身类型的类型，包含一个以参数化类型返回自身引用的方法。
 * 可以用于嵌套的、流畅调用的 Builder 类型以便于编译器保证类型安全
 *
 * @author Azige
 * @param <SELF> 扩展此类型的类型
 * @see https://stackoverflow.com/questions/39015564/java-generic-builder
 */
public abstract class SelfTyped<SELF extends SelfTyped<SELF>> {

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }
}
