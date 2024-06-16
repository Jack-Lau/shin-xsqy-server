/*
 * Created 2018-12-14 16:30:13
 */
package cn.com.yting.kxy.web.game.mingjiandahui.resource;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class KingBattleEverydayAward implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long name;
    @XmlElements(@XmlElement(name = "award", type = Award.class))
    private List<Award> awards;
    @XmlElement
    private long mail;

    private List<CurrencyStack> currencyStacks;

    public List<CurrencyStack> getCurrencyStacks() {
        if (currencyStacks == null) {
            currencyStacks = awards.stream()
                .map(it -> new CurrencyStack(it.id, it.amount))
                .collect(Collectors.toList());
        }
        return currencyStacks;
    }

    @Getter
    public static class Award {

        @XmlElement
        private long id;
        @XmlElement
        private long amount;
    }
}
