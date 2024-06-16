/*
 * Created 2017-7-7 16:55:49
 */
package cn.com.yting.kxy.battle.generate;

import java.util.Collections;

import cn.com.yting.kxy.core.IdClassifier;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceReference;


/**
 * 此引用可以用来引用 UnitDescriptor 或 UnitDescriptorSelector。
 * 如果引用的资源是 UnitDescriptor，会被包装成 UnitDescriptor
 *
 * @author Azige
 */
public class UnitDescriptorSelectorWrapperReference implements ResourceReference<UnitDescriptorSelector>{

    private static IdClassifier idClassifier = IdClassifier.getInstance()
        .copyPart(UnitDescriptor.class, UnitDescriptorSelector.class);
    private ResourceContext resourceContext;
    private long id;
    private Class<? extends Resource> type;

    public UnitDescriptorSelectorWrapperReference(ResourceContext resourceContext, long id){
        type = (Class<? extends Resource>)idClassifier.classifyType(id);
        if (type == null){
            throw new IllegalArgumentException("资源id不是此引用能处理的类型，id=" + id);
        }
        this.resourceContext = resourceContext;
        this.id = id;
    }

    @Override
    public long getId(){
        return id;
    }

    @Override
    public Class<UnitDescriptorSelector> getType(){
        return UnitDescriptorSelector.class;
    }

    @Override
    public boolean exists(){
        return resourceContext.getLoader(type).exists(id);
    }

    @Override
    public UnitDescriptorSelector get(){
        if (type.equals(UnitDescriptor.class)){
            UnitDescriptor unitDescriptor = resourceContext.getLoader(UnitDescriptor.class).get(id);
            return new UnitDescriptorSelector(id, () -> Collections.singleton(unitDescriptor));
        }else if (type.equals(UnitDescriptorSelector.class)){
            return resourceContext.getLoader(UnitDescriptorSelector.class).get(id);
        }else{
            throw new AssertionError("Impossible");
        }
    }
}
