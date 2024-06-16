/*
 * Created 2018-9-17 15:21:45
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class EquipmentProduceLoader extends XmlMapContainerResourceLoader<EquipmentProduce> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().forEach(it -> it.buildGenerators(context));
    }

    @Override
    public String getDefaultResourceName() {
        return "sEquipmentProduce.xml";
    }

    @Override
    public Class<EquipmentProduce> getSupportedClass() {
        return EquipmentProduce.class;
    }

}
