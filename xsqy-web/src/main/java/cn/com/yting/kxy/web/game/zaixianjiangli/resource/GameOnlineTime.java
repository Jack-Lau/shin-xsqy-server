/*
 * Created 2019-1-23 15:26:35
 */
package cn.com.yting.kxy.web.game.zaixianjiangli.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GameOnlineTime implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long time;
    @XmlElement
    private long award;
    @XmlElement
    private Currency currency;

    @Getter
    public static class Currency {

        @XmlElement
        private long Id;
        @XmlElement
        private String name;
        @XmlElement
        private long amount;
    }
}
