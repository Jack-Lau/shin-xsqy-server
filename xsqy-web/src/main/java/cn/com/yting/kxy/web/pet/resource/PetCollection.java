/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.pet.resource;

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

/**
 *
 * @author Darkholme
 */
@Getter
public class PetCollection implements Resource {

    private static class LazyInit {

        private static final IdClassifier idClassifier = IdClassifier.getInstance().copyPart(PetCollection.class);
    }

    @XmlAttribute
    private long id;
    @XmlElement
    private int random;
    @XmlElements(
            @XmlElement(name = "pet", type = Pet.class)
    )
    private List<Pet> pets;

    ResourceContext resourceContext;
    private RandomSelector<Long> selector;

    public long getOnePrototypeId() {
        if (selector == null) {
            RandomSelectorBuilder<Long> builder = RandomSelector.builder();
            pets.forEach((pet) -> {
                if (PetCollection.class.equals(LazyInit.idClassifier.classifyType(pet.id))) {
                    builder.add(() -> Collections.singleton(resourceContext.getLoader(PetCollection.class).get(pet.id).getOnePrototypeId()), pet.probability);
                }
                builder.add(pet.getId(), pet.getProbability());
            });
            selector = builder.build(random == 0 ? RandomSelectType.INDEPENDENT : RandomSelectType.DEPENDENT);
        }
        return selector.getSingle();
    }

    @Getter
    public static class Pet {

        @XmlElement
        private long id;
        @XmlElement
        private double probability;
    }

}
