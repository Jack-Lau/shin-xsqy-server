/*
 * Created 2016-1-14 15:47:32
 */
package cn.com.yting.kxy.battle.generate;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class BattleDescriptorLoader extends XmlMapContainerResourceLoader<BattleDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(BattleDescriptorLoader.class);

    @Override
    public String getDefaultResourceName() {
        return "sBattle.xml";
    }

    @Override
    public void reload(ResourceContext context, InputStream input) {
        BattleXml battleXml = JAXB.unmarshal(input, BattleXml.class);
        setMap(
                battleXml.datas.stream()
                        .map(data -> {
                            BattleDescriptor battleDescriptor = new BattleDescriptor();
                            battleDescriptor.setId(data.id);
                            battleDescriptor.setHpVisible(data.showHp == 1);
                            battleDescriptor.setSinglePlayerLimited(data.singleBattle == 1);
                            battleDescriptor.setMonsterPartyDescriptorRef(context.createReference(PartyDescriptor.class, data.monsterTeamId));
                            return battleDescriptor;
                        })
                        .collect(Collectors.toMap(BattleDescriptor::getId, Function.identity()))
        );
    }

    @Override
    public Class<BattleDescriptor> getSupportedClass() {
        return BattleDescriptor.class;
    }

    @XmlRootElement(name = "serverRoot")
    static class BattleXml {

        @XmlElements(
                @XmlElement(name = "data", type = BattleData.class)
        )
        List<BattleData> datas;
    }

    static class BattleData {

        @XmlAttribute
        long id;
        @XmlElement
        String name;
        @XmlElement
        int canOperate;
        @XmlElement
        int showHp;
        @XmlElement
        int singleBattle;
        @XmlElement
        int allowOtherFactionSupporter;
        @XmlElement
        long monsterTeamId;
        @XmlElement
        long NPC1;
        @XmlElement
        long NPC2;
        @XmlElement
        long NPC3;
        @XmlElement
        long NPC4;
        @XmlElement
        long NPC5;
        @XmlElement
        long eventHandler1;
        @XmlElement
        long eventHandler2;
        @XmlElement
        long eventHandler3;
        @XmlElement
        long eventHandler4;
        @XmlElement
        long eventHandler5;
        @XmlElement
        long eventHandler6;
        @XmlElement
        long eventHandler7;
    }
}
