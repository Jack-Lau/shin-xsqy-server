/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion.resource;

import cn.com.yting.kxy.core.resource.Resource;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class FashionDyeCost implements Resource {

    @XmlAttribute
    private long id;
    @XmlElements(
            @XmlElement(name = "dyeCost", type = DyeCost.class))
    private List<DyeCost> dyeCost;

    public DyeCost getDyeCost(int part) {
        for (DyeCost dc : dyeCost) {
            if (dc.getPart() == part) {
                return dc;
            }
        }
        return null;
    }

    @Getter
    public static class DyeCost {

        @XmlElement
        private int part;
        @XmlElement
        private long colorCost;
        @XmlElement
        private long saturationCost;
        @XmlElement
        private long brightnessCost;
    }

}
