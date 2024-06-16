/*
 * Created 2018-9-17 15:21:30
 */
package cn.com.yting.kxy.web.equipment.resource;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.SimpleParameter;
import cn.com.yting.kxy.core.random.NormalRandomGenerator;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class EquipmentProduce implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int color;
    @XmlElement
    private int part;
    @XmlElements(
        @XmlElement(name = "attr", type = Attr.class)
    )
    private List<Attr> attrs;
    @XmlElement
    private int randomModel;
    @XmlElement
    private long strengthenFourEffect;
    @XmlElement
    private long strengthenTenEffect;

    private NormalRandomGenerator normalRandomGenerator;

    void buildGenerators(ResourceContext resourceContext) {
        normalRandomGenerator = NormalRandomGenerator.createByStochasticModel(resourceContext, randomModel);
    }

    public List<Parameter> generateParameters() {
        return attrs.stream()
            .map(it -> new SimpleParameter(it.getOutputAttribute(), normalRandomGenerator.generateRanged(it.getOutputMin(), it.getOutputMax())))
            .collect(Collectors.toList());
    }

    public int generateMaxEnhanceLevel() {
        Random random = RandomProvider.getRandom();
        double randomNumber = random.nextDouble();
        int baseLevel = (color - 2) * 3;
        if (randomNumber < 0.35) {
            baseLevel += 1;
        } else if (randomNumber < 0.7) {
            baseLevel += 2;
        } else {
            baseLevel += 3;
        }
        return baseLevel;
    }

    @Getter
    public static class Attr {

        @XmlElement
        private String outputAttribute;
        @XmlElement
        private int outputMin;
        @XmlElement
        private int outputMax;
    }
}
