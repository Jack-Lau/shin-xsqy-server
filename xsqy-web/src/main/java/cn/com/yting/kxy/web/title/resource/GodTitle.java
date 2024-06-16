/*
 * Created 2018-11-16 21:07:28
 */
package cn.com.yting.kxy.web.title.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GodTitle implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long name;
    @XmlElement
    private long prototypeId;
    @XmlElement
    private Integer nowNumber;
}
