/*
 * Created 2015-11-13 16:07:55
 */
package cn.com.yting.kxy.core.parameter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * 用于描述一些参数集合的类。参数空间描述一些从名字到{@link ParameterBase 参数基元}的映射，
 * 可以通过嵌套或叠加之类的方式组合一些参数空间来描述一些特定场景下的参数组合。
 * <p>
 * 例如：游戏中的角色拥有一个根参数空间，他的角色能力、装备等等模块各自有自己的参数空间，
 * 这些参数空间嵌套在根参数空间中，角色的综合参数是这些参数空间中的参数的合计值。
 *
 * @author Azige
 */
public interface ParameterSpace extends JsonSerializable{

    /**
     * <b>零空间</b>，此参数空间对于任何名字都返回{@link ParameterBase#ZERO 参数基元零值}。
     */
    ParameterSpace EMPTY = new ParameterSpace(){

        @Override
        public ParameterBase getParameterBase(String name){
            return ParameterBase.ZERO;
        }

        @Override
        public Map<String, ? extends ParameterBase> toMap(){
            return Collections.emptyMap();
        }
    };

    /**
     * 以名字检索一个参数基元。如果没有映射的话可以返回{@link ParameterBase#ZERO 参数基元零值}，
     * 但不能返回null。
     *
     * @param name 要检索的名字
     * @return 对应的参数基元
     */
    ParameterBase getParameterBase(String name);

    /**
     * 将参数空间转换成 Map 对象。
     * 转换出的对象包含参数空间当前的所有映射，并且不受此对象的变化影响。
     *
     * @return 与此对象当前状态相同的 Map 对象
     */
    Map<String, ? extends ParameterBase> toMap();

    /**
     * 返回此对象的 {@link RootParameterSpace} 的表示形式。
     * 此对象的变化会影响到返回的对象。
     *
     * @return 此对象的 RootParameterSpace 的表示形式。
     */
    default RootParameterSpace asRootParameterSpace(){
        if (this instanceof RootParameterSpace){
            return (RootParameterSpace)this;
        }
        return new RootParameterSpace(){

            @Override
            public NamedParameterBase getParameterBase(String name){
                return new SimpleNamedParameterBase(name, ParameterSpace.this.getParameterBase(name));
            }

            @Override
            public Map<String, NamedParameterBase> toMap(){
                Map<String, ? extends ParameterBase> map = ParameterSpace.this.toMap();
                return map.entrySet().stream()
                    .collect(Collectors.toMap(Entry::getKey, e -> new SimpleNamedParameterBase(e.getKey(), e.getValue())));
            }
        };
    }

    @Override
    public default void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException{
        gen.writeObject(asRootParameterSpace());
    }

    @Override
    public default void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static ParameterSpaceBuilder builder() {
        return new ParameterSpaceBuilder();
    }
}
