/*
 * Created 2016-1-7 11:22:22
 */
package cn.com.yting.kxy.battle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 *
 * @author Azige
 * @param <PB>
 */
public class PartyBuilder<PB extends PartyBuilder<PB>>{

    private Map<Integer, UnitBuilder<?>> unitBuilderMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static PartyBuilder<?> create(){
        return new PartyBuilder();
    }

    public PB unit(int position, UnitBuilder<?> ub){
        unitBuilderMap.put(position, ub);
        return chainObject();
    }

    public UnitBuilder<?> unit(int position){
        UnitBuilder<?> ub = UnitBuilder.create();
        unit(position, ub);
        return ub;
    }

    public Party build(){
        return new Party(
            unitBuilderMap.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().build()))
        );
    }

    @SuppressWarnings("unchecked")
    protected PB chainObject(){
        return (PB)this;
    }
}
