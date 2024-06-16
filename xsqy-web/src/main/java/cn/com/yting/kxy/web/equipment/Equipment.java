/*
 * Created 2018-8-9 17:20:51
 */
package cn.com.yting.kxy.web.equipment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameter;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.equipment.resource.EquipmentEffect;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSoulLevel;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSoulName;
import cn.com.yting.kxy.web.equipment.resource.EquipmentStrengthening;
import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "equipment", indexes = {
    @Index(columnList = "account_id"),
    @Index(columnList = "nft_id")
})
@Data
@WebMessageType
public class Equipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @JsonIgnore
    @Column(name = "base_parameters_text", nullable = false)
    private String baseParametersText = "";
    @Column(name = "base_fc", nullable = false)
    private int baseFc;
    @Column(name = "effects_text", nullable = false)
    private String effectsText = "";
    @Column(name = "enhance_level", nullable = false)
    private int enhanceLevel = 0;
    @Column(name = "max_enhance_level", nullable = false)
    private int maxEnhanceLevel;
    @Column(name = "highest_enhance_level_ever", nullable = false)
    private int highestEnhanceLevelEver = 0;
    @Column(name = "creator_name")
    private String creatorName;
    @Column(name = "nft_id", unique = true)
    private Long nftId;
    @Column(name = "number")
    private Integer number;
    @Column(name = "next_withdraw_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextWithdrawTime;
    //
    @Column(name = "soul_level", nullable = false)
    private int soulLevel = 0;
    @Column(name = "soul_exp", nullable = false)
    private long soulExp = 0;
    @Column(name = "soul_name_1")
    private String soulName_1;
    @Column(name = "soul_name_id_1")
    private Long soulNameId_1;
    @Column(name = "soul_name_2")
    private String soulName_2;
    @Column(name = "soul_name_id_2")
    private Long soulNameId_2;
    @Column(name = "soul_name_3")
    private String soulName_3;
    @Column(name = "soul_name_id_3")
    private Long soulNameId_3;

    /**
     * 导入基础参数集合，另外会计算基础战斗力
     *
     * @param parameters
     */
    public void importBaseParameters(List<Parameter> parameters) {
        baseParametersText = parameters.stream()
                .map(it -> it.getName() + ":" + it.getValue())
                .collect(Collectors.joining(","));
        baseFc = (int) parameters.stream()
                .mapToDouble(it -> it.getValue() * EquipmentConstants.PARAMETER_NAME_TO_FC_FACTOR.getOrDefault(it.getName(), 0d))
                .sum();
    }

    public List<Long> exportEffectIds() {
        return CommaSeparatedLists.fromText(effectsText, Long::valueOf);
    }

    /**
     * 导入装备特效，与现有的特效列表合并
     *
     * @param equipmentEffects
     */
    public void importAndMergeEffects(Collection<EquipmentEffect> equipmentEffects) {
        importAndMergeEffectIds(equipmentEffects.stream()
                .map(EquipmentEffect::getId)
                .collect(Collectors.toList()));
    }

    public void importEffects(Collection<EquipmentEffect> equipmentEffects) {
        effectsText = equipmentEffects.stream()
                .map(EquipmentEffect::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void importAndMergeEffectIds(Collection<Long> equipmentEffectIds) {
        Stream<String> currentEffectStream;
        if (effectsText.isEmpty()) {
            currentEffectStream = Stream.empty();
        } else {
            currentEffectStream = Arrays.stream(effectsText.split(","));
        }
        effectsText = Stream.concat(
                currentEffectStream,
                equipmentEffectIds.stream()
                        .map(String::valueOf)
        )
                .distinct()
                .collect(Collectors.joining(","));
    }

    public List<Parameter> getBaseParameters() {
        List<Parameter> list = new ArrayList<>();
        for (String parameterText : baseParametersText.split(",")) {
            String[] fragments = parameterText.split(":");
            if (fragments.length != 2) {
                throw new IllegalStateException("无法解析的文本：" + parameterText);
            }
            String name = fragments[0];
            double value = Double.parseDouble(fragments[1]);
            list.add(new SimpleParameter(name, value));
        }
        return list;
    }

    public ParameterSpace createParameterSpace(ResourceContext resourceContext) {
        List<ParameterSpace> parameterSpaces = new ArrayList<>();
        //
        Map<String, ParameterBase> map = new HashMap<>();
        EquipmentStrengthening equipmentStrengthening = resourceContext.getLoader(EquipmentStrengthening.class).get(enhanceLevel);
        for (String parameterText : baseParametersText.split(",")) {
            String[] fragments = parameterText.split(":");
            if (fragments.length != 2) {
                throw new IllegalStateException("无法解析的文本：" + parameterText);
            }
            String name = fragments[0];
            double value = Double.parseDouble(fragments[1]);
            map.put(name, new SimpleParameterBase(value * (1 + equipmentStrengthening.getAbility())));
        }
        map.put(ParameterNameConstants.战斗力, new SimpleParameterBase(baseFc * (1 + equipmentStrengthening.getFc())));
        parameterSpaces.add(new SimpleParameterSpace(map));
        //
        ResourceLoader<EquipmentEffect> equipmentEffectLoader = resourceContext.getLoader(EquipmentEffect.class);
        CommaSeparatedLists.fromText(effectsText, Long::valueOf).stream()
                .map(equipmentEffectLoader::get)
                .map(EquipmentEffect::getParameterSpace)
                .forEach(parameterSpaces::add);
        //
        Map<String, ParameterBase> soulMap;
        EquipmentSoulLevel equipmentSoulLevel = resourceContext.getLoader(EquipmentSoulLevel.class).get(soulLevel);
        if (soulName_1 != null && soulNameId_1 != null) {
            soulMap = new HashMap<>();
            EquipmentSoulName equipmentSoulName = resourceContext.getLoader(EquipmentSoulName.class).get(soulNameId_1);
            soulMap.put(getParameterNameBySoulName(soulName_1), new SimpleParameterBase(equipmentSoulLevel.getParameterBySoulName(soulName_1) * equipmentSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(equipmentSoulLevel.getFc() * equipmentSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_2 != null && soulNameId_2 != null) {
            soulMap = new HashMap<>();
            EquipmentSoulName equipmentSoulName = resourceContext.getLoader(EquipmentSoulName.class).get(soulNameId_2);
            soulMap.put(getParameterNameBySoulName(soulName_2), new SimpleParameterBase(equipmentSoulLevel.getParameterBySoulName(soulName_2) * equipmentSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(equipmentSoulLevel.getFc() * equipmentSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_3 != null && soulNameId_3 != null) {
            soulMap = new HashMap<>();
            EquipmentSoulName equipmentSoulName = resourceContext.getLoader(EquipmentSoulName.class).get(soulNameId_3);
            soulMap.put(getParameterNameBySoulName(soulName_3), new SimpleParameterBase(equipmentSoulLevel.getParameterBySoulName(soulName_3) * equipmentSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(equipmentSoulLevel.getFc() * equipmentSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        //
        return new AggregateParameterSpace(parameterSpaces);
    }

    public void increaseEnhanceLevel() {
        enhanceLevel++;
        if (enhanceLevel > highestEnhanceLevelEver) {
            highestEnhanceLevelEver = enhanceLevel;
        }
    }

    public void decreaseEnhanceLevel() {
        enhanceLevel--;
    }

    public void verifyOwner(long accountId) throws KxyWebException {
        if (this.accountId != accountId) {
            throw KxyWebException.unknown("不是装备的所有者");
        }
    }

    public EquipmentDetail toDetail(ResourceContext resourceContext) {
        return new EquipmentDetail(this, createParameterSpace(resourceContext).asRootParameterSpace().toParameters());
    }

    public String getParameterNameBySoulName(String soulName) {
        switch (soulName) {
            case "外伤":
                return ParameterNameConstants.物伤;
            case "内伤":
                return ParameterNameConstants.法伤;
            case "外防":
                return ParameterNameConstants.物防;
            case "内防":
                return ParameterNameConstants.法防;
            case "气血":
                return ParameterNameConstants.最大生命;
            case "幸运":
                return ParameterNameConstants.幸运;
            case "速度":
                return ParameterNameConstants.速度;
            case "招式":
                return ParameterNameConstants.招式力;
            case "抵抗":
                return ParameterNameConstants.抵抗力;
            case "连击":
                return ParameterNameConstants.连击率;
            case "吸血":
                return ParameterNameConstants.吸血率;
            case "暴击":
                return ParameterNameConstants.暴击率;
            case "暴效":
                return ParameterNameConstants.暴击效果;
            case "招架":
                return ParameterNameConstants.格挡率;
            case "神佑":
                return ParameterNameConstants.神佑率;
        }
        return ParameterNameConstants.最大生命;
    }

}
