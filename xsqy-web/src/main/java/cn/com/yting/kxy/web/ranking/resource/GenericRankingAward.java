/*
 * Created 2018-10-31 11:13:05
 */
package cn.com.yting.kxy.web.ranking.resource;

import java.util.ArrayList;
import java.util.List;

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
public class GenericRankingAward implements Resource {

    @XmlAttribute
    private long id;
    @XmlElements(
        @XmlElement(name = "model", type = Model.class)
    )
    private List<Model> models;

    @Getter
    public static class Model {

        @XmlElement
        private long Id;
        @XmlElement
        private long way;
        @XmlElement
        private long currency1;
        @XmlElement
        private double parameter1;
        @XmlElement
        private long currency2;
        @XmlElement
        private double parameter2;

        public List<CurrencyStack> toCurrencyStacks() {
            List<CurrencyStack> list = new ArrayList<>();
            if (currency1 != 0) {
                list.add(new CurrencyStack(currency1, (long) parameter1));
            }
            if (currency2 != 0) {
                list.add(new CurrencyStack(currency2, (long) parameter2));
            }
            return list;
        }

        public List<CurrencyStack> toCurrencyStacks(long rebateMilliKuaibiFromOther) {
            List<CurrencyStack> list = new ArrayList<>();
            if (currency1 != 0) {
                list.add(new CurrencyStack(currency1, (long) (0.1 * rebateMilliKuaibiFromOther * parameter1)));
            }
            if (currency2 != 0) {
                list.add(new CurrencyStack(currency2, (long) (0.1 * rebateMilliKuaibiFromOther * parameter2)));
            }
            return list;
        }
    }
}
