/*
 * Created 2018-9-12 12:15:19
 */
package cn.com.yting.kxy.web.school.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class SchoolAbilityConsumption implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long contribution;
    @XmlElement
    private long totalContribution;

}
