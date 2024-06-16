/*
 * Created 2016-1-10 17:27:10
 */
package cn.com.yting.kxy.battle.generate;

import java.util.Collection;
import java.util.function.Supplier;

import cn.com.yting.kxy.core.resource.Resource;


/**
 *
 * @author Azige
 */
public class UnitDescriptorSelector implements Resource{

    private long id;
    private Supplier<Collection<UnitDescriptor>> supplier;

    public UnitDescriptorSelector(long id, Supplier<Collection<UnitDescriptor>> supplier){
        this.id = id;
        this.supplier = supplier;
    }

    @Override
    public long getId(){
        return id;
    }

    public Collection<UnitDescriptor> getCollection(){
        return supplier.get();
    }
}
