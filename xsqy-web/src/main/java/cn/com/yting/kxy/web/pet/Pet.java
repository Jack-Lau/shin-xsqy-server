/*
 * Created 2018-10-10 17:08:25
 */
package cn.com.yting.kxy.web.pet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import cn.com.yting.kxy.core.parameter.FilterParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.parameter.SingleElementParameterSpace;
import cn.com.yting.kxy.core.parameter.resource.AttributesLoader;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.pet.resource.PetAbilityInformation;
import cn.com.yting.kxy.web.pet.resource.PetAddStar;
import cn.com.yting.kxy.web.pet.resource.PetSoulLevel;
import cn.com.yting.kxy.web.pet.resource.PetSoulName;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "pet", indexes = {
    @Index(columnList = "account_id, sorting_index desc"),
    @Index(columnList = "nft_id")
})
@Data
@WebMessageType
public class Pet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @Column(name = "pet_name", nullable = false)
    private String petName;
    @Column(name = "pet_rank", nullable = false)
    private int rank;
    @Column(name = "rank_progress", nullable = false)
    private int rankProgress;
    @Column(name = "max_rank", nullable = false)
    private int maxRank;
    @Column(name = "abilities_text", nullable = false, length = 1000)
    @JsonIgnore
    private String abilitiesText = "";
    @Column(name = "max_ability_capacity", nullable = false)
    private int maxAbilityCapacity;
    @Column(name = "aptitude_hp", nullable = false)
    private int aptitudeHp;
    @Column(name = "aptitude_atk", nullable = false)
    private int aptitudeAtk;
    @Column(name = "aptitude_pdef", nullable = false)
    private int aptitudePdef;
    @Column(name = "aptitude_mdef", nullable = false)
    private int aptitudeMdef;
    @Column(name = "aptitude_spd", nullable = false)
    private int aptitudeSpd;
    @Column(name = "number")
    private Integer number;
    @Column(name = "sorting_index", nullable = false)
    private int sortingIndex;
    @Column(name = "candidate_abilities_text", nullable = false, length = 1000)
    @JsonIgnore
    private String candidateAbilitiesText = "";
    @Column(name = "nft_id", unique = true)
    private Long nftId;
    @Column(name = "next_withdraw_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextWithdrawTime;
    @Column(name = "legendary", nullable = false)
    private boolean legendary;
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
    @Column(name = "soul_name_4")
    private String soulName_4;
    @Column(name = "soul_name_id_4")
    private Long soulNameId_4;
    @Column(name = "soul_name_5")
    private String soulName_5;
    @Column(name = "soul_name_id_5")
    private Long soulNameId_5;
    @Column(name = "soul_name_6")
    private String soulName_6;
    @Column(name = "soul_name_id_6")
    private Long soulNameId_6;

    public List<Long> getAbilities() {
        return CommaSeparatedLists.fromText(abilitiesText, Long::valueOf);
    }

    public void importAbilities(List<Long> abilityIds) {
        this.abilitiesText = CommaSeparatedLists.toText(abilityIds);
    }

    public List<Long> getCandidateAbilities() {
        return CommaSeparatedLists.fromText(candidateAbilitiesText, Long::valueOf);
    }

    public void importCandidateAbilities(List<Long> abilityIds) {
        this.candidateAbilitiesText = CommaSeparatedLists.toText(abilityIds);
    }

    public void updateSortingIndex() {
        sortingIndex = PetConstants.sortingIndex(aptitudeHp, aptitudeAtk, aptitudePdef, aptitudeMdef, aptitudeSpd);
    }

    public ParameterSpace createParameterSpace(int referenceLevel, ResourceContext resourceContext, ParameterSpace extraParameterSpace) {
        Map<String, ParameterBase> map = new HashMap<>();
        double rankBooster = getRankBooster(resourceContext);
        map.put(ParameterNameConstants.最大生命, apptitudeToParameter(aptitudeHp, referenceLevel, rankBooster, 400, 10, 0.086, 1000));
        map.put(ParameterNameConstants.物伤, apptitudeToParameter(aptitudeAtk, referenceLevel, rankBooster, 400, 10, 0.018, 200));
        map.put(ParameterNameConstants.法伤, apptitudeToParameter(aptitudeAtk, referenceLevel, rankBooster, 400, 10, 0.018, 200));
        map.put(ParameterNameConstants.物防, apptitudeToParameter(aptitudePdef, referenceLevel, rankBooster, 400, 10, 0.011, 130));
        map.put(ParameterNameConstants.法防, apptitudeToParameter(aptitudeMdef, referenceLevel, rankBooster, 400, 10, 0.011, 130));
        map.put(ParameterNameConstants.速度, apptitudeToParameter(aptitudeSpd, referenceLevel, rankBooster, 400, 10, 0.0055, 20));
        ParameterSpace baseSpace = new SimpleParameterSpace(map);
        //
        List<ParameterSpace> parameterSpaces = new ArrayList<>();
        parameterSpaces.add(resourceContext.getByLoaderType(AttributesLoader.class).getPetBaseParameterSpace());
        parameterSpaces.add(baseSpace);
        parameterSpaces.add(createBaseFcParameterSpace(referenceLevel, rankBooster));
        parameterSpaces.add(createAbilitiesParameterSpace(referenceLevel, resourceContext));
        parameterSpaces.add(extraParameterSpace);
        //
        Map<String, ParameterBase> soulMap;
        PetSoulLevel petSoulLevel = resourceContext.getLoader(PetSoulLevel.class).get(soulLevel);
        if (soulName_1 != null && soulNameId_1 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_1);
            soulMap.put(getParameterNameBySoulName(soulName_1), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_1) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_2 != null && soulNameId_2 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_2);
            soulMap.put(getParameterNameBySoulName(soulName_2), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_2) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_3 != null && soulNameId_3 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_3);
            soulMap.put(getParameterNameBySoulName(soulName_3), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_3) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_4 != null && soulNameId_4 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_4);
            soulMap.put(getParameterNameBySoulName(soulName_4), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_4) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_5 != null && soulNameId_5 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_5);
            soulMap.put(getParameterNameBySoulName(soulName_5), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_5) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        if (soulName_6 != null && soulNameId_6 != null) {
            soulMap = new HashMap<>();
            PetSoulName petSoulName = resourceContext.getLoader(PetSoulName.class).get(soulNameId_6);
            soulMap.put(getParameterNameBySoulName(soulName_6), new SimpleParameterBase(petSoulLevel.getParameterBySoulName(soulName_6) * petSoulName.getFactor()));
            soulMap.put(ParameterNameConstants.战斗力, new SimpleParameterBase(petSoulLevel.getFc() * petSoulName.getFactor()));
            parameterSpaces.add(new SimpleParameterSpace(soulMap));
        }
        //
        return new AggregateParameterSpace(parameterSpaces);
    }

    private double getRankBooster(ResourceContext resourceContext) {
        return 1 + resourceContext.getLoader(PetAddStar.class).getAll().values().stream()
                .filter(it -> it.getStarLevel() == rank && it.getStarStage() == rankProgress)
                .findAny()
                .map(it -> it.getPomotion())
                .orElse(0d);
    }

    private ParameterSpace createBaseFcParameterSpace(int referenceLevel, double rankBooster) {
        int fc = (int) (rankBooster * (int) ((referenceLevel + 400) * (sortingIndex + 100) * 0.004 + 490));
        return new SingleElementParameterSpace(ParameterNameConstants.战斗力, new SimpleParameterBase(fc));
    }

    private ParameterSpace createAbilitiesParameterSpace(int referenceLevel, ResourceContext resourceContext) {
        ResourceLoader<PetAbilityInformation> abilityLoader = resourceContext.getLoader(PetAbilityInformation.class);
        return CommaSeparatedLists.fromText(abilitiesText, Long::valueOf).stream()
                .map(abilityId -> abilityLoader.get(abilityId).createParameterSpace(referenceLevel))
                .collect(Collectors.collectingAndThen(Collectors.toList(), AggregateParameterSpace::new));
    }

    public ParameterSpace createFcParameterSpace(int referenceLevel, ResourceContext resourceContext) {
        return new AggregateParameterSpace(
                createBaseFcParameterSpace(referenceLevel, getRankBooster(resourceContext)),
                new FilterParameterSpace(ParameterNameConstants.战斗力, createAbilitiesParameterSpace(referenceLevel, resourceContext))
        );
    }

    private ParameterBase apptitudeToParameter(int aptitude, int referenceLevel, double rankBooster, int const1, int const2, double const3, int const4) {
        return new SimpleParameterBase(rankBooster * (int) (Math.max(1, (referenceLevel + const1) * (aptitude + const2) * const3 + const4)));
    }

    public void verifyOwner(long accountId) {
        if (getAccountId() != accountId) {
            throw PetException.notOwner();
        }
    }

    public PetDetail toDetail() {
        return new PetDetail(this, null);
    }

    public PetDetail toExtraDetail(PlayerRepository playerRepository, ResourceContext resourceContext) {
        return new PetDetail(
                this,
                createParameterSpace(PetConstants.referenceLevel(accountId, playerRepository), resourceContext, ParameterSpace.EMPTY).asRootParameterSpace().toParameters()
        );
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
