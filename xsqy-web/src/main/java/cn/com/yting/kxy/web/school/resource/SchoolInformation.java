/*
 * Created 2018-9-12 15:54:42
 */
package cn.com.yting.kxy.web.school.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class SchoolInformation implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String school;
    @XmlElements(
        @XmlElement(name = "ability", type = Ability.class)
    )
    private List<Ability> abilities;
    @XmlElement
    private long selfAi;
    @XmlElement
    private long nonSelfAi;

    @Getter
    public static class Ability {

        @XmlElement
        private long abilityId;
        @XmlElement
        private String abilityName;

    }
}
