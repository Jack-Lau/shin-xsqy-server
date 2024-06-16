/*
 * Created 2018-9-17 16:52:08
 */
package cn.com.yting.kxy.core.random.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class StochasticModel implements Resource {

    @XmlAttribute
    private long id;
    @XmlElements(@XmlElement(name = "stochasticModel", type = Double.class))
    private List<Double> stochasticModels;

    public double getValueByIndex(int index) {
        return stochasticModels.get(index - 1);
    }
}
