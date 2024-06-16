/*
 * Created 2018-11-22 16:35:47
 */
package cn.com.yting.kxy.web.impartation.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class ContributionAndExpGetProportion implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private double expProportion;
    @XmlElement
    private double contributionProportion;
}
