/*
 * Created 2018-7-12 16:48:42
 */
package cn.com.yting.kxy.web.currency.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.currency.CurrencyDefinition;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class Currency implements Resource, CurrencyDefinition {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private long storeLimit;

    @Override
    public long getCurrencyId() {
        return id;
    }

    @Override
    public long getMaxAmount() {
        return storeLimit;
    }

    public static Currency getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(Currency.class).get(id);
    }
}
