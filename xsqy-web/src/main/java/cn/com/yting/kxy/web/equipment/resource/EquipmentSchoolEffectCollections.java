/*
 * Created 2018-9-19 18:12:45
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
public class EquipmentSchoolEffectCollections extends EquipmentEffectCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int random;
    @XmlElements(
        @XmlElement(name = "schoolEffect", type = SchoolEffect.class)
    )
    private List<SchoolEffect> schoolEffects;

    private RandomSelector<EquipmentEffect> selector;

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<EquipmentEffect> builder = RandomSelector.builder();
        for (SchoolEffect schoolEffect : schoolEffects) {
            Class<?> type = IdClassifier.getInstance().classifyType(schoolEffect.id);
            if (EquipmentEffect.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends EquipmentEffect> ref = resourceContext.createReference((Class<? extends EquipmentEffect>) type, schoolEffect.id);
                builder.add(() -> Collections.singleton(ref.get()), schoolEffect.probability);
            } else if (EquipmentEffectCollectionSupplier.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends EquipmentEffectCollectionSupplier> ref = resourceContext.createReference((Class<? extends EquipmentEffectCollectionSupplier>) type, schoolEffect.id);
                builder.add(() -> ref.get().get(), schoolEffect.probability);
            }
        }
        selector = builder.build(random == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    @Override
    public Collection<EquipmentEffect> get() {
        return selector.get();
    }

    public static class SchoolEffect {

    @XmlElement
        private long id;
    @XmlElement
        private String name;
    @XmlElement
        private double probability;
    }
}
