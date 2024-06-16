/*
 * Created 2019-1-23 10:59:56
 */
package cn.com.yting.kxy.web.game.fuxingjianglin.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class LuckyStarAwardInfo implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private String mailItem;
    @XmlElement
    private long award;

    @Getter
    public static class Currency {

        @XmlElement
        private long Id;
        @XmlElement
        private String name;
        @XmlElement
        private int amount;
    }
}
