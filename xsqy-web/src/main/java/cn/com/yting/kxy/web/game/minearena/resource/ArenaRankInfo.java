/*
 * Created 2018-10-18 11:33:22
 */
package cn.com.yting.kxy.web.game.minearena.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class ArenaRankInfo implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private int rank;
    @XmlElement
    private long currency;
    @XmlElement
    private double factor;

}
