/*
 * Created 2018-10-10 17:03:49
 */
package cn.com.yting.kxy.web.pet.resource;

import java.util.List;
import java.util.Random;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
// c宠物信息表.xlsx revision 11399
@Getter
public class PetInformations implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String petName;
    @XmlElement
    private int prefabId;
    @XmlElement
    private String modelScale;
    @XmlElement
    private int color;
    @XmlElement
    private int type;
    @XmlElement
    private int limited;
    @XmlElement
    private int 生命资质下限;
    @XmlElement
    private int 生命资质上限;
    @XmlElement
    private int 攻击资质下限;
    @XmlElement
    private int 攻击资质上限;
    @XmlElement
    private int 物防资质下限;
    @XmlElement
    private int 物防资质上限;
    @XmlElement
    private int 速度资质下限;
    @XmlElement
    private int 速度资质上限;
    @XmlElement
    private int 法防资质下限;
    @XmlElement
    private int 法防资质上限;
    @XmlElement
    private int maxAbilityCount;
    @XmlElement
    private int normalStochasticModel;
    @XmlElement
    private int addStarModel;
    @XmlElement
    private int maxAddStar;
    @XmlElement
    private int minAddStar;
    @XmlElements(@XmlElement(name = "activeSkill", type = ActiveSkill.class))
    private List<ActiveSkill> activeSkills;
    @XmlElement
    private long initialSkillOne;
    @XmlElement
    private long initialSkillTwo;
    @XmlElement
    private long normalSkillCollections;
    @XmlElement
    private long activationSkillCollections;
    @XmlElement
    private String description;

    public int generateMaxRank() {
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
        return Math.min(12, baseLevel);
    }

    @Getter
    public static class ActiveSkill {

        @XmlElement
        private long id;
        @XmlElement
        private long AI;
    }
}
