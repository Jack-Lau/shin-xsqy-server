/*
 * Created 2018-8-18 15:56:05
 */
package cn.com.yting.kxy.battle.skill.resource;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.skill.DamageSkillPerformer;
import cn.com.yting.kxy.battle.skill.ChimeraSkill;
import cn.com.yting.kxy.battle.skill.MultiTargetSelectors;
import cn.com.yting.kxy.battle.skill.RecoverSkillPerformer;
import cn.com.yting.kxy.battle.skill.ResourceSkillPerformer;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.SkillParameterTable;
import cn.com.yting.kxy.battle.skill.SkillPerformer;
import cn.com.yting.kxy.battle.skill.TargetTableFunctions;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceReference;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class SkillParam implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private int elementType;
    @XmlElement
    private int templateType;
    @XmlElement
    private String templateParam;
    @XmlElement
    private String cost;
    @XmlElement
    private int targetType;
    @XmlElement
    private int multiTargetChooseType;
    @XmlElement
    private int allowMultiHit;
    @XmlElement
    private int maxTargetCount;
    @XmlElement
    private int maxAffectCount;

    @XmlElement
    private double fixHitRate;
    @XmlElement
    private double basicHitRate;
    @XmlElement
    private double basicCoefficient;
    @XmlElement
    private double processCountCoefficient;
    @XmlElement
    private double basicValueLevelCoefficient;
    @XmlElement
    private double basicValueConstant;
    @XmlElement
    private double multiTargetCoefficient;
    @XmlElement
    private double multiTargetRatio;

    @XmlElement
    private double defPenetrationRate;
    @XmlElement
    private double extraCriticalRate;
    @XmlElement
    private double extraMultiHitRate;

    private ResourceReference<ResourceSkillPerformer> specialSkillPerformerRef;

    public void injectSpecialSkillPerformerRef(ResourceReference<ResourceSkillPerformer> ref) {
        this.specialSkillPerformerRef = ref;
    }

    public enum SkillType {

        NORMAL,
        FURY
    }

    public enum ElementType {

        无,
        风,
        雷,
        火,
        土
    }

    public SkillType getSkillType() {
        switch (type) {
            case 1:
                return SkillType.NORMAL;
            case 2:
                return SkillType.FURY;
            default:
                throw new IllegalStateException("无效的技能类型：" + type);
        }
    }

    public ElementType getElementType() {
        switch (elementType) {
            case 0:
                return ElementType.无;
            case 1:
                return ElementType.风;
            case 2:
                return ElementType.雷;
            case 3:
                return ElementType.火;
            case 4:
                return ElementType.土;
            default:
                throw new IllegalStateException("无效的元素类型：" + elementType);
        }
    }

    public boolean canMultiHit() {
        return allowMultiHit == 1;
    }

    public boolean checkTargetTable(Unit source, Unit target) {
        switch (targetType) {
            case 1:
                return TargetTableFunctions.敌方_活着的(source, target);
            case 2:
                return TargetTableFunctions.敌方_死亡的(source, target);
            case 3:
                return TargetTableFunctions.友方_活着的(source, target);
            case 4:
                return TargetTableFunctions.友方_死亡的(source, target);
            case 5:
                return TargetTableFunctions.敌方(source, target);
            case 6:
                return TargetTableFunctions.友方(source, target);
            default:
                throw new IllegalStateException("无效的目标类型：" + targetType);
        }
    }

    public List<Unit> selectSecondaryTargets(Unit main, List<Unit> optionalTargets) {
        switch (multiTargetChooseType) {
            case 1:
                return MultiTargetSelectors.默认选择策略(main, optionalTargets);
            case 2:
                return MultiTargetSelectors.按hp比例从低到高(main, optionalTargets);
            case 3:
                return MultiTargetSelectors.多重随机选择(main, optionalTargets, maxTargetCount);
            case 4:
                return MultiTargetSelectors.优先选择可封印(main, optionalTargets);
            default:
                throw new IllegalStateException("无效的多目标选择策略：" + multiTargetChooseType);
        }
    }

    public SkillPerformer getSkillPerformer() {
        switch (templateType) {
            //造成伤害
            case 1:
                return new DamageSkillPerformer(templateParam);
            //造成回复
            case 2:
                return new RecoverSkillPerformer(false);
            //复活并回复
            case 3:
                return new RecoverSkillPerformer(true);
            //特殊技能（BUFF相关）
            case 4:
                return specialSkillPerformerRef.get();
            default:
                return new DamageSkillPerformer(templateParam);
        }
    }

    public Skill createSkill(int level) {
        Skill skill = new ChimeraSkill(this, getSkillPerformer());
        skill.setLevel(level);
        return skill;
    }

    public SkillParameterTable toSkillParameterTable() {
        return SkillParameterTable.builder()
                .id(id)
                .元素类型(getElementType())
                .固定命中率(fixHitRate)
                .基础命中率(basicHitRate)
                .基础系数(basicCoefficient)
                .回数系数(processCountCoefficient)
                .基础值等级系数(basicValueLevelCoefficient)
                .基础值常数(basicValueConstant)
                .多目标衰减系数(multiTargetCoefficient)
                .多目标衰减比例(multiTargetRatio)
                .防御穿透率(defPenetrationRate)
                .额外暴击率(extraCriticalRate)
                .额外连击率(extraMultiHitRate)
                .build();
    }

}
