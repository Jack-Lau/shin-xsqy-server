/*
 * Created 2016-1-11 16:02:19
 */
package cn.com.yting.kxy.battle.generate;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import cn.com.yting.kxy.core.IdClassifier;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class UnitDescriptorSelectorLoader extends XmlMapContainerResourceLoader<UnitDescriptorSelector>{

    private static final Logger LOG = LoggerFactory.getLogger(UnitDescriptorSelectorLoader.class);
    private IdClassifier idClassifier = IdClassifier.getInstance().copyPart(
        UnitDescriptor.class,
        UnitDescriptorSelector.class
    );
    private ResourceContext resourceContext;

    @Override
    public String getDefaultResourceName(){
        return "sMonsterCollections.xml";
    }

    @Override
    public void reload(ResourceContext context, InputStream input){
        resourceContext = context;
        MonsterCollectionXml monsterCollectionXml = JAXB.unmarshal(input, MonsterCollectionXml.class);
        setMap(
            monsterCollectionXml.datas.stream()
                .collect(Collectors.toMap(data -> data.id, data -> {
                    RandomSelectorBuilder<UnitDescriptor> builder = RandomSelector.builder();
                    data.monsters.forEach(monster -> {
                        if (monster.id == 0){
                            builder.add(Collections::emptyList, monster.weigh);
                        }else{
                            UnitDescriptorSelectorWrapperReference ref = new UnitDescriptorSelectorWrapperReference(resourceContext, monster.id);
                            builder.add(() -> ref.get().getCollection(), monster.weigh);
                        }
                    });
                    return new UnitDescriptorSelector(data.id, builder.build(RandomSelectType.DEPENDENT));
                }))
        );
    }

    @Override
    public Class<UnitDescriptorSelector> getSupportedClass(){
        return UnitDescriptorSelector.class;
    }

    @XmlRootElement(name = "serverRoot")
    static class MonsterCollectionXml{

        @XmlElements(
            @XmlElement(name = "data", type = MonsterCollectionData.class)
        )
        List<MonsterCollectionData> datas;
    }

    static class MonsterCollectionData{

        @XmlAttribute
        long id;
        @XmlElement
        String name;
        @XmlElements(
            @XmlElement(name = "monster", type = MonsterEntry.class)
        )
        List<MonsterEntry> monsters;

        @Override
        public String toString(){
            return "MonsterCollectionData{" + "id=" + id + ", name=" + name + ", monster=" + monsters + '}';
        }
    }

    static class MonsterEntry{

        @XmlElement
        long id;
        @XmlElement
        String name;
        @XmlElement
        int weigh;
        @XmlElement
        String remarks;

        @Override
        public String toString(){
            return "MonsterEntry{" + "id=" + id + ", name=" + name + ", weigh=" + weigh + ", remarks=" + remarks + '}';
        }
    }
}
