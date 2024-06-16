/*
 * Created 2019-1-8 11:30:12
 */
package cn.com.yting.kxy.web.perk.resource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class TalentTrainModel implements Resource {

    private static final List<Function<TalentTrainModel, Integer>> GETTERS = Arrays.asList(
        TalentTrainModel::getPositionStar1,
        TalentTrainModel::getPositionStar2,
        TalentTrainModel::getPositionStar3,
        TalentTrainModel::getPositionStar4,
        TalentTrainModel::getPositionStar5,
        TalentTrainModel::getPositionStar6,
        TalentTrainModel::getPositionStar7,
        TalentTrainModel::getPositionStar8,
        TalentTrainModel::getPositionStar9
    );

    @XmlAttribute
    private long id;
    @XmlElement
    private int positionStar1;
    @XmlElement
    private int positionStar2;
    @XmlElement
    private int positionStar3;
    @XmlElement
    private int positionStar4;
    @XmlElement
    private int positionStar5;
    @XmlElement
    private int positionStar6;
    @XmlElement
    private int positionStar7;
    @XmlElement
    private int positionStar8;
    @XmlElement
    private int positionStar9;

    public int getPositionStar(int index) {
        return GETTERS.get(index).apply(this);
    }
}
