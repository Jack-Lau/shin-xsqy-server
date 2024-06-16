/*
 * Created 2018-7-26 15:39:19
 */
package cn.com.yting.kxy.web.gift.resource;

import java.time.Instant;
import java.util.List;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.KxyConstants;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.validperiod.AbsoluteValidPeriod;
import cn.com.yting.kxy.core.validperiod.ValidPeriod;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GiftAndExchangeCodeInformation implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int amount;
    @XmlElement
    private int convertibility;
    @XmlElements(
        @XmlElement(name = "curry", type = Curry.class)
    )
    private List<Curry> curries;
    @XmlElement
    private long mail;
    @XmlElement
    private String effectDate;
    @XmlElement
    private String invalidDate;

    private ValidPeriod validPeriod;

    void afterUnmarshal(Unmarshaller u, Object parent) {
        validPeriod = new AbsoluteValidPeriod(
            Instant.from(KxyConstants.KXY_DATE_TIME_FORMATTER.parse(effectDate)),
            Instant.from(KxyConstants.KXY_DATE_TIME_FORMATTER.parse(invalidDate))
        );
    }

    @Getter
    public static class Curry {

        @XmlElement
        private long id;
        @XmlElement
        private int amount;
    }
}
