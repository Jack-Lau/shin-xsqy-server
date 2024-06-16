/*
 * Created 2018-10-12 11:02:01
 */
package cn.com.yting.kxy.web.pet.resource;

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
public class PetSkillCollections extends PetAbilityInformationCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String collectionName;
    @XmlElement
    private int random;
    @XmlElements(@XmlElement(name = "petSkill", type = PetSkill.class))
    private List<PetSkill> petSkills;

    RandomSelector<PetAbilityInformation> selector;

    void buildSelector(ResourceContext resourceContext) {
        RandomSelectorBuilder<PetAbilityInformation> builder = RandomSelector.builder();
        for (PetSkill petSkill : petSkills) {
            Class<?> type = IdClassifier.getInstance().classifyType(petSkill.petSkillId);
            if (PetAbilityInformation.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends PetAbilityInformation> ref = resourceContext.createReference((Class<? extends PetAbilityInformation>) type, petSkill.petSkillId);
                builder.add(() -> Collections.singleton(ref.get()), petSkill.weigh);
            } else if (PetSkillCollections.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ResourceReference<? extends PetSkillCollections> ref = resourceContext.createReference((Class<? extends PetSkillCollections>) type, petSkill.petSkillId);
                builder.add(() -> ref.get().get(), petSkill.weigh);
            }
        }
        selector = builder.build(random == 1 ? RandomSelectType.DEPENDENT : RandomSelectType.INDEPENDENT);
    }

    @Override
    public Collection<PetAbilityInformation> get() {
        return selector.get();
    }

    @Getter
    public static class PetSkill {

        @XmlElement
        private long petSkillId;
        @XmlElement
        private String petSkillName;
        @XmlElement
        private int weigh;

    }
}
