/*
 * Created 2016-1-10 17:03:05
 */
package cn.com.yting.kxy.battle.generate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import cn.com.yting.kxy.core.parameter.resource.Attributes;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Azige
 */
public class UnitDescriptorLoader extends XmlMapContainerResourceLoader<UnitDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(UnitDescriptorLoader.class);

    @Override
    public String getDefaultResourceName() {
        return "sMonster.xml";
    }

    @Override
    public void reload(ResourceContext context, InputStream input) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            MonsterXml monsterXml = JAXB.unmarshal(new DOMSource(document), MonsterXml.class);
            XPath xPath = XPathFactory.newInstance().newXPath();
            Map<String, String> defaultParamsMap = new HashMap<>();
            for (Attributes a : context.getLoader(Attributes.class).getAll().values()) {
                defaultParamsMap.put(a.getName(), String.valueOf(a.getDefaultValueOfMonster()));
            }
            for (MonsterData data : monsterXml.datas) {
                Node node = (Node) xPath.compile(String.format("/serverRoot/data[@id=%d]/parameters", data.id)).evaluate(document, XPathConstants.NODE);
                NodeList childNodes = node.getChildNodes();
                Map<String, String> parameterMap = new HashMap<>(defaultParamsMap);
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node parameterExprNode = childNodes.item(i);
                    if (parameterExprNode.getNodeType() == Node.ELEMENT_NODE) {
                        parameterMap.put(parameterExprNode.getNodeName(), parameterExprNode.getTextContent());
                    }
                }
                for (OtherAttribute oa : data.otherAttributes) {
                    parameterMap.put(oa.name, oa.value);
                }
                data.parameterMap = parameterMap;
            }

            setMap(
                    monsterXml.datas.stream()
                            .map(data -> {
                                UnitDescriptor unitDescriptor = new UnitDescriptor();
                                unitDescriptor.setId(data.id);
                                unitDescriptor.setName(data.name);
                                unitDescriptor.setTitleId(data.title);
                                unitDescriptor.setSkillIds(data.skillElements.stream().map(e -> e.skillId).collect(Collectors.toList()));
                                unitDescriptor.setPrefabId(data.prefabId);
                                unitDescriptor.setWeaponSerialId(data.weaponPrefabId);
                                if (data.modelScale > 0) {
                                    unitDescriptor.setModelScale(data.modelScale);
                                } else {
                                    unitDescriptor.setModelScale(1.0);
                                }
                                unitDescriptor.setLevelExpr(data.level);
                                unitDescriptor.setParameterExprMap(data.parameterMap);
                                unitDescriptor.setRobotId(data.aiId);
                                unitDescriptor.setFlyable(data.flyOut == 1);
                                return unitDescriptor;
                            })
                            .collect(Collectors.toMap(UnitDescriptor::getId, Function.identity()))
            );
        } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException ex) {
            throw new RuntimeException("xml解析错误", ex);
        }
    }

    @Override
    public Class<UnitDescriptor> getSupportedClass() {
        return UnitDescriptor.class;
    }

    @XmlRootElement(name = "serverRoot")
    static class MonsterXml {

        @XmlElements(
                @XmlElement(name = "data", type = MonsterData.class)
        )
        List<MonsterData> datas;
    }

    static class MonsterData {

        @XmlAttribute
        long id;
        @XmlElement
        String name;
        @XmlElement
        long title;
        @XmlElement
        long prefabId;
        @XmlElement
        int weaponPrefabId;
        @XmlElement
        double modelScale;
        @XmlElement
        int flyOut;
        @XmlElement
        Long aiId;
        @XmlElement
        String level;
        @XmlElements(
                @XmlElement(name = "skill", type = MonsterSkillElement.class)
        )
        List<MonsterSkillElement> skillElements = new ArrayList<>();
        @XmlElements(
                @XmlElement(name = "otherAttribute", type = OtherAttribute.class)
        )
        List<OtherAttribute> otherAttributes = new ArrayList<>();

        Map<String, String> parameterMap;

        @Override
        public String toString() {
            return "MonsterData{" + "id=" + id + ", name=" + name + ", prefabId=" + prefabId + ", weaponPrefabId=" + weaponPrefabId + ", modelScale=" + modelScale
                    + ", color=" + flyOut + ", ai=" + aiId + ", level=" + level + ", parameters=" + parameterMap + '}';
        }
    }

    static class MonsterSkillElement {

        @XmlElement
        long skillId;
    }

    static class OtherAttribute {

        @XmlElement
        String name;
        @XmlElement
        String value;

    }

}
