/*
 * Created 2016-1-13 11:27:39
 */
package cn.com.yting.kxy.battle.generate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import cn.com.yting.kxy.battle.generate.PartyDescriptor.UnitEntry;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class PartyDescriptorLoader extends XmlMapContainerResourceLoader<PartyDescriptor>{

    private static final Logger LOG = LoggerFactory.getLogger(PartyDescriptorLoader.class);
    Map<Long, MonsterTeamData> map;

    @Override
    public String getDefaultResourceName(){
        return "sMonsterTeam.xml";
    }

    @Override
    public void reload(ResourceContext context, InputStream input){
        MonsterTeamXml monsterTeamXml = JAXB.unmarshal(input, MonsterTeamXml.class);
        setMap(
            monsterTeamXml.datas.stream()
                .map(data -> {
                    Map<Integer, UnitEntry> monsterMap = new HashMap<>();
                    int position = 0;
                    for (MonsterEntry monster : data.monsters){
                        position++;
                        // 空位置
                        if (monster.id == -1) {
                            continue;
                        }
                        UnitDescriptorSelectorWrapperReference unitDescriptorRef = new UnitDescriptorSelectorWrapperReference(context, monster.id);
                        monsterMap.put(position, new UnitEntry(() -> unitDescriptorRef.get().getCollection().iterator().next(), monster.minNumberRequired));
                    }
                    return new PartyDescriptor(data.id, monsterMap);
                })
                .collect(Collectors.toMap(PartyDescriptor::getId, Function.identity()))
        );
    }

    @Override
    public Class<PartyDescriptor> getSupportedClass(){
        return PartyDescriptor.class;
    }

    @XmlRootElement(name = "serverRoot")
    static class MonsterTeamXml{

        @XmlElements(
            @XmlElement(name = "data", type = MonsterTeamData.class)
        )
        List<MonsterTeamData> datas;
    }

    static class MonsterTeamData{

        @XmlAttribute
        long id;
        @XmlElement
        String name;
        @XmlElement
        String type;
        @XmlElement
        int teamLevel;
        @XmlElements(
            @XmlElement(name = "monster", type = MonsterEntry.class)
        )
        List<MonsterEntry> monsters;
    }

    static class MonsterEntry{

        @XmlElement
        long id;
        @XmlElement
        String name;
        @XmlElement
        int minNumberRequired;
    }
}
