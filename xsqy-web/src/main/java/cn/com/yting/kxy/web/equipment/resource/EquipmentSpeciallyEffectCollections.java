/*
 * Created 2018-9-19 17:22:07
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.IdClassifier;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceReference;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class EquipmentSpeciallyEffectCollections extends EquipmentEffectCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int random;
    @XmlElements(
        @XmlElement(name = "speciallyEffect", type = SpeciallyEffect.class)
    )
    private List<SpeciallyEffect> speciallyEffects;

    private RandomSelector<EquipmentEffect> selector;

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<EquipmentEffect> builder = RandomSelector.builder();
        for (SpeciallyEffect speciallyEffect : speciallyEffects) {
            Class<?> type = IdClassifier.getInstance().classifyType(speciallyEffect.id);
            if (EquipmentEffect.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends EquipmentEffect> ref = resourceContext.createReference((Class<? extends EquipmentEffect>) type, speciallyEffect.id);
                builder.add(() -> Collections.singleton(ref.get()), speciallyEffect.probability);
            } else if (EquipmentEffectCollectionSupplier.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends EquipmentEffectCollectionSupplier> ref = resourceContext.createReference((Class<? extends EquipmentEffectCollectionSupplier>) type, speciallyEffect.id);
                builder.add(() -> ref.get().get(), speciallyEffect.probability);
            }
        }
        selector = builder.build(random == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    @Override
    public Collection<EquipmentEffect> get() {
        return selector.get();
    }

    @Getter
    public static class SpeciallyEffect {

        @XmlElement
        private long id;
        @XmlElement
        private String name;
        @XmlElement
        private double probability;
    }

}
