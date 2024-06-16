/*
 * Created 2018-8-10 18:22:49
 */
package cn.com.yting.kxy.web.player.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class PlayerLevelupExp implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long exp;
    @XmlElement
    private long sumExp;
}
