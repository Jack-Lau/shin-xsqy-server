/*
 * Created 2015-11-30 18:17:53
 */
package cn.com.yting.kxy.core.parameter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * 根参数空间作为参数空间与其它模块连接的出口，可以根据名字检索并直接导出一个参数， 或导出整个空间中的所有参数的集合。
 *
 * @author Azige
 */
public interface RootParameterSpace extends ParameterSpace, JsonSerializable {

    @Override
    NamedParameterBase getParameterBase(String name);

    /**
     * 获得一个与此空间关联的参数。 参数的值会随着此空间的状态变化。
     *
     * @param name 参数名字
     * @return 参数
     */
    default Parameter getParameter(String name) {
        return new Parameter() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public double getValue() {
                return getParameterBase(name).exportValue();
            }

            @Override
            public int compareTo(Parameter t) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    Map<String, NamedParameterBase> toMap();

    /**
     * 将根参数空间转换成参数的集合。 转换出的对象包含参数空间当前的所有映射构成的参数，并且不受此对象的变化影响。
     *
     * @return 根据此对象当前状态生成的参数列表
     */
    default List<Parameter> toParameters() {
        return toMap().values().stream()
                .map(NamedParameterBase::toParameter)
                .collect(Collectors.toList());
    }

    @Override
    default void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        Map<String, NamedParameterBase> map = toMap();
        for (Entry<String, NamedParameterBase> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            NamedParameterBase value = entrySet.getValue();
            gen.writeStartObject();
            gen.writeObjectField("name", key);
            gen.writeObjectField("value", value.toParameter().getValue());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }

    @Override
    default void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
