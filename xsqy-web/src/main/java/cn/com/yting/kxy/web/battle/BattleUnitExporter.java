/*
 * Created 2018-9-27 17:47:41
 */
package cn.com.yting.kxy.web.battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.battle.BattleDirectorBuilder.BattlePartyBuilder;
import cn.com.yting.kxy.battle.BattleDirectorBuilder.BattlePartyBuilder.BattleUnitBuilder;
import cn.com.yting.kxy.battle.PartyBuilder;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.battle.skill.resource.SkillParamLoader;
import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterBase;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SimpleParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.web.drug.DrugService;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.fashion.Fashion;
import cn.com.yting.kxy.web.fashion.FashionDye;
import cn.com.yting.kxy.web.fashion.FashionDyeRepository;
import cn.com.yting.kxy.web.fashion.FashionRepository;
import cn.com.yting.kxy.web.party.PartyConstants;
import cn.com.yting.kxy.web.party.SupportRelation;
import cn.com.yting.kxy.web.party.SupportRelationRepository;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetConstants;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.pet.resource.PetAbilityInformation;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.pet.resource.PetInformations.ActiveSkill;
import cn.com.yting.kxy.web.player.ParameterSpaceProviderBus;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.school.SchoolRecord;
import cn.com.yting.kxy.web.school.SchoolRepository;
import cn.com.yting.kxy.web.title.TitleRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class BattleUnitExporter {

    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SupportRelationRepository supportRelationRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private FashionRepository fashionRepository;
    @Autowired
    private FashionDyeRepository fashionDyeRepository;

    @Autowired
    private DrugService drugService;

    @Autowired
    private ParameterSpaceProviderBus parameterSpaceProviderBus;
    @Autowired
    private ResourceContext resourceContext;

    private final Cache<SupportRelation.PK, UnitBuilder<?>> supportUnitCache = CacheBuilder.newBuilder()
            .expireAfterAccess(PartyConstants.DURATION_SUPPORT)
            .build();

    public void exportPlayer(
            Player player,
            UnitBuilder<?> ub,
            Stance stance,
            boolean usePlayerAi,
            boolean openSummon) {
        SkillParamLoader skillParamLoader = (SkillParamLoader) (resourceContext.getLoader(SkillParam.class));
        RootParameterSpace rps = parameterSpaceProviderBus.createRootSpace(player.getAccountId());
        Optional<PlayerRelation> optionalPlayerRelation = playerRelationRepository.findById(player.getAccountId());
        //
        Fashion fashion = optionalPlayerRelation.map(it -> it.getFashionId())
                .flatMap(it -> fashionRepository.findById(it))
                .orElse(null);
        FashionDye fashionDye = null;
        if (fashion != null && fashion.getDyeId() != 0) {
            fashionDye = fashionDyeRepository.findById(fashion.getDyeId()).orElse(null);
            if (fashionDye == null || fashionDye.getAccountId() != player.getAccountId()) {
                fashionDye = null;
            }
        }
        //
        ub
                .id(player.getAccountId())
                .sourceId(player.getAccountId())
                //
                .type(Unit.UnitType.TYPE_PLAYER)
                .stance(stance)
                //
                .name(player.getPlayerName())
                .prefabId(player.getPrefabId())
                .weaponPrefabId(
                        optionalPlayerRelation
                                .map(it -> it.getHandEquipmentId() == null ? null : equipmentRepository.findById(it.getHandEquipmentId()).orElse(null))
                                .map(Equipment::getDefinitionId)
                                .orElse(null)
                )
                .titleId(
                        optionalPlayerRelation
                                .map(it -> it.getTitleId())
                                .flatMap(it -> titleRepository.findById(it))
                                .map(it -> it.getDefinitionId())
                                .orElse(null)
                )
                .fashionId(fashion != null ? fashion.getDefinitionId() : null)
                .fashionDye(fashionDye != null ? fashionDye.toBattleUnitFashionDye() : null)
                .parameter(rps.toParameters())
                .attackSkill();
        //
        SchoolRecord schoolRecord = schoolRepository.findById(player.getAccountId()).orElse(null);
        if (schoolRecord == null) {
            ub
                    .skill(skillParamLoader.get(100101).createSkill(player.getPlayerLevel()))
                    .skill(skillParamLoader.get(100201).createSkill(player.getPlayerLevel()))
                    .robot(BattleConstants.ROBOT_MAP.get(usePlayerAi).get(100L));
        } else {
            ub
                    .skills(schoolRecord.createSkills(resourceContext))
                    .robot(BattleConstants.ROBOT_MAP.get(usePlayerAi).get(schoolRecord.getSchoolId()));
        }
        //
        if (openSummon) {
            List<Pet> battlePets = playerRelationRepository.findOrDummy(player.getAccountId()).toBattlePetIds().stream()
                    .map(id -> Optional.ofNullable(id)
                    .flatMap(petRepository::findById)
                    .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (int i = 0; i < battlePets.size(); i++) {
                exportPet(ub, player, battlePets.get(i), stance, true);
            }
        }
        //
    }

    public void exportSinglePlayerParty(
            BattlePartyBuilder partyBuilder,
            Player player,
            Stance stance,
            boolean usePlayerAi,
            boolean openSummon,
            int position) {
        BattleUnitBuilder playerBuilder = partyBuilder.unit(position);
        exportPlayer(player, playerBuilder, stance, usePlayerAi, openSummon);
        if (!openSummon) {
            exportPets(partyBuilder, player, stance);
        }
    }

    public void exportPlayerPartyWithSupport(
            BattlePartyBuilder partyBuilder,
            Player player,
            Stance stance,
            boolean usePlayerAi,
            boolean openSummon) {
        List<SupportRelation> supportRelations = supportRelationRepository.findPartyMembers(player.getAccountId()).stream()
                .sorted(Comparator.comparing(it -> it.getDeadline()))
                .limit(2)
                .collect(Collectors.toList());
        BattleUnitBuilder playerBuilder = partyBuilder.unit(1);
        exportPlayer(player, playerBuilder, stance, usePlayerAi, openSummon);
        for (int i = 0; i < supportRelations.size(); i++) {
            SupportRelation supportRelation = supportRelations.get(i);
            UnitBuilder<?> ub = getOrCreateSupportUnit(supportRelation.getInviterAccountId(), supportRelation.getSupporterAccountId());
            partyBuilder.unit(i + 2, ub);
        }
        if (!openSummon) {
            exportPets(partyBuilder, player, stance);
        }
    }

    public void createSupportUnit(long inviterAccountId, long supporterAccountId) {
        SupportRelation.PK pk = new SupportRelation.PK();
        pk.setInviterAccountId(inviterAccountId);
        pk.setSupporterAccountId(supporterAccountId);
        supportUnitCache.put(pk, createSupportUnitInternal(supporterAccountId));
    }

    private UnitBuilder<?> getOrCreateSupportUnit(long inviterAccountId, long supporterAccountId) {
        SupportRelation.PK pk = new SupportRelation.PK();
        pk.setInviterAccountId(inviterAccountId);
        pk.setSupporterAccountId(supporterAccountId);
        try {
            return supportUnitCache.get(pk, () -> createSupportUnitInternal(supporterAccountId));
        } catch (ExecutionException ex) {
            throw (RuntimeException) ex.getCause();
        }
    }

    private UnitBuilder<?> createSupportUnitInternal(long supporterAccountId) {
        UnitBuilder<?> ub = UnitBuilder.create();
        exportPlayer(playerRepository.findById(supporterAccountId).get(), ub, Stance.STANCE_RED, false, false);
        return ub;
    }

    public void exportPets(
            PartyBuilder<?> pb,
            Player player,
            Stance stance) {
        List<Pet> battlePets = playerRelationRepository.findOrDummy(player.getAccountId()).toBattlePetIds().stream()
                .map(id -> Optional.ofNullable(id)
                .flatMap(petRepository::findById)
                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (int i = 0; i < battlePets.size(); i++) {
            UnitBuilder<?> ub = pb.unit(1 + BattleConstants.PET_POSITION_OFFSET + i);
            exportPet(ub, player, battlePets.get(i), stance, false);
        }
    }

    private void exportPet(
            UnitBuilder<?> ub,
            Player owner,
            Pet pet,
            Stance stance,
            boolean openSummon) {
        RootParameterSpace rps = parameterSpaceProviderBus.createRootSpace(owner.getAccountId());
        //
        List<ParameterSpace> parameterSpaces = new ArrayList<>();
        Map<String, ParameterBase> map = new HashMap<>();
        map.merge(ParameterNameConstants.招式力, new SimpleParameterBase(rps.getParameter(ParameterNameConstants.御兽招式力).getValue()), ParameterBase::plus);
        map.merge(ParameterNameConstants.抵抗力, new SimpleParameterBase(rps.getParameter(ParameterNameConstants.御兽抵抗力).getValue()), ParameterBase::plus);
        parameterSpaces.add(new SimpleParameterSpace(map));
        parameterSpaces.add(drugService.createPetParameterSpace(owner.getAccountId()));
        List<Parameter> parameters = pet.createParameterSpace(PetConstants.referenceLevel(owner),
                resourceContext,
                new AggregateParameterSpace(parameterSpaces)).asRootParameterSpace().toParameters();
        //
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
        ActiveSkill activeSkill;
        if (pet.getRank() < 10) {
            activeSkill = petInformations.getActiveSkills().get(0);
        } else {
            activeSkill = petInformations.getActiveSkills().get(1);
        }
        ResourceLoader<PetAbilityInformation> abilityLoader = resourceContext.getLoader(PetAbilityInformation.class);
        ResourceLoader<SkillParam> skillLoader = resourceContext.getLoader(SkillParam.class);
        Robot robot = resourceContext.getLoader(Robot.class).get(activeSkill.getAI());
        List<Skill> skills = Stream.concat(Stream.of(activeSkill.getId()), pet.getAbilities().stream())
                .map(id -> abilityLoader.get(id).getSkillId())
                .filter(Objects::nonNull)
                .map(id -> skillLoader.get(id).createSkill(1))
                .collect(Collectors.toList());
        if (openSummon) {
            ub
                    .petUnit()
                    .sourceId(pet.getId())
                    //
                    .type(Unit.UnitType.TYPE_PET)
                    .stance(stance)
                    .flyable()
                    //
                    .name(pet.getPetName())
                    .prefabId(petInformations.getPrefabId())
                    //
                    .robot(robot)
                    .attackSkill()
                    .skills(skills)
                    //
                    .parameter(parameters);
        } else {
            ub
                    .sourceId(pet.getId())
                    //
                    .type(Unit.UnitType.TYPE_PET)
                    .stance(stance)
                    .flyable()
                    //
                    .name(pet.getPetName())
                    .prefabId(petInformations.getPrefabId())
                    //
                    .robot(robot)
                    .attackSkill()
                    .skills(skills)
                    //
                    .parameter(parameters);
        }
    }

}
