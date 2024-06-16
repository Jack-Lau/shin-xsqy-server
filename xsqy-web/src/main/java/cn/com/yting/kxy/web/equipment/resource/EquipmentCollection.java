/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.IdClassifier;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Darkholme
 */
@Getter
public class EquipmentCollection implements Resource {

    private static class LazyInit {

        private static final IdClassifier idClassifier = IdClassifier.getInstance().copyPart(EquipmentCollection.class);
    }

    @XmlAttribute
    private long id;
    @XmlElement
    private int random;
    @XmlElements(
            @XmlElement(name = "equipment", type = Equipment.class)
    )
    private List<Equipment> equipments;

    ResourceContext resourceContext;
    private RandomSelector<Long> selector;

    public long getOnePrototypeId() {
        if (selector == null) {
            RandomSelectorBuilder<Long> builder = RandomSelector.builder();
            equipments.forEach((equipment) -> {
                if (EquipmentCollection.class.equals(LazyInit.idClassifier.classifyType(equipment.id))) {
                    builder.add(() -> Collections.singleton(resourceContext.getLoader(EquipmentCollection.class).get(equipment.id).getOnePrototypeId()), equipment.probability);
                } else {
                    builder.add(equipment.getId(), equipment.getProbability());
                }
            });
            selector = builder.build(random == 0 ? RandomSelectType.INDEPENDENT : RandomSelectType.DEPENDENT);
        }
        return selector.getSingle();
    }

    @Getter
    public static class Equipment {

        @XmlElement
        private long id;
        @XmlElement
        private double probability;
    }

}
