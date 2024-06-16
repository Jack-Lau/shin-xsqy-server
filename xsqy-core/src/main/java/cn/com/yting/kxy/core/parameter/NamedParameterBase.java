/*
 * Created 2015-11-30 18:15:41
 */
package cn.com.yting.kxy.core.parameter;

/**
 * 已命名的参数基元。可以直接转换成参数。
 *
 * @author Azige
 */
public interface NamedParameterBase extends ParameterBase{

    /**
     * 获得名字。
     *
     * @return 名字
     */
    String getName();

    /**
     * 转换成参数。参数的名字是此对象的名字，值是参数基元导出值。
     *
     * @return 对应的参数
     */
    default Parameter toParameter(){
        return new SimpleParameter(getName(), exportValue());
    }
}
