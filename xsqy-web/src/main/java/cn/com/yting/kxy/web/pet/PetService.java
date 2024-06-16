/*
 * Created 2018-10-10 17:01:34
 */
package cn.com.yting.kxy.web.pet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.random.NormalRandomGenerator;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.random.resource.StochasticModelLoader;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.wordfilter.ForbiddenWordsChecker;
import cn.com.yting.kxy.web.KxyWebConstants;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.drug.DrugService;
import cn.com.yting.kxy.web.pet.resource.GodPet;
import cn.com.yting.kxy.web.pet.resource.PetAbilityInformation;
import cn.com.yting.kxy.web.pet.resource.PetAbilityInformationCollectionSupplier;
import cn.com.yting.kxy.web.pet.resource.PetAbilityStudy;
import cn.com.yting.kxy.web.pet.resource.PetAddStar;
import cn.com.yting.kxy.web.pet.resource.PetAddStarLoader;
import cn.com.yting.kxy.web.pet.resource.PetCollection;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.pet.resource.PetSkillCollections;
import cn.com.yting.kxy.web.pet.resource.PetSoulLevel;
import cn.com.yting.kxy.web.pet.resource.PetSoulName;
import cn.com.yting.kxy.web.pet.resource.PetSoulPart;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerException;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.price.PriceConstants;
import cn.com.yting.kxy.web.price.PriceService;
import cn.com.yting.kxy.web.recycling.RecyclingResult;
import cn.com.yting.kxy.web.recycling.resource.WasteRecovery;
import cn.com.yting.kxy.web.recycling.resource.WasteRecoveryLoader;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class PetService implements InitializingBean, ParameterSpaceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PetService.class);

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PriceService priceService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private DrugService drugService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ForbiddenWordsChecker forbiddenWordsChecker;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Queue<PetGachaLog> latestInterestingGachas = EvictingQueue.create(10);

    private RandomSelector<Long> petSoulNameJackpot;
    private List<RandomSelector<String>> petSoulPartJackpot;

    public List<Parameter> getPetParameters(long petId) {
        return petRepository.findById(petId)
                .map(pet -> pet.createParameterSpace(PetConstants.referenceLevel(pet.getAccountId(), playerRepository),
                resourceContext,
                drugService.createPetParameterSpace(pet.getAccountId())).asRootParameterSpace().toParameters())
                .orElse(null);
    }

    public void modifyBattleList(long accountId, List<Long> petIds) {
        petIds = petIds.stream()
                .distinct()
                .peek(id -> petRepository.findById(id)
                .orElseThrow(() -> KxyWebException.notFound("宠物不存在，id=" + id))
                .verifyOwner(accountId))
                .collect(Collectors.toList());

        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        playerRelation.importBattlePetIdList(petIds);
    }

    public Pet rename(long accountId, long petId, String newName) {
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        if (!KxyWebConstants.PLAYER_NAME_PATTERN.matcher(newName).matches() || forbiddenWordsChecker.check(newName)) {
            throw PlayerException.playerNameIllegal(newName);
        }
        pet.verifyOwner(accountId);

        pet.setPetName(newName);
        return pet;
    }

    public PetDetail gacha(long accountId, long expectedPrice) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        if (player.getPlayerLevel() < PetConstants.PLAYER_LEVEL_PREREQUIREMENT) {
            LOG.info("未达到等级试图使用宠物获得，accountId={}", accountId);
            throw KxyWebException.unknown("等级未达到要求");
        }
        String playerName = player.getPlayerName();
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, PetConstants.PRICE_GACHA, true, CurrencyConstants.PURPOSE_DECREMENT_获得宠物);
        priceService.deduct(accountId, PriceConstants.ID_宠物获得, expectedPrice, CurrencyConstants.PURPOSE_DECREMENT_获得宠物);
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_积分, PetConstants.POINT_REWARD_PET_GACHA);

        Random random = RandomProvider.getRandom();
        int randomNumber = random.nextInt(10000) + 1;
        int color;
        if (randomNumber <= 7000) {
            color = 2;
        } else if (randomNumber <= 9899) {
            color = 3;
        } else if (randomNumber <= 9999) {
            color = 4;
        } else {
            color = 5;
        }
        List<Long> candidates = PetConstants.PET_GENERATOR_MAP.get(color);
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("没有备选宠物，color=" + color);
        }

        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(candidates.get(random.nextInt(candidates.size())));
        if (PetConstants.DEFINITION_IDS_BINGO.contains(petInformations.getId())) {
            Map<String, Object> args = new HashMap<>();
            args.put("playerName", playerName);
            args.put("petName", petInformations.getPetName());
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            PetConstants.BROADCAST_ID_GACHA_BINGO,
                            args
                    )
            );
        }
        if (color == 4 || color == 5) {
            synchronized (latestInterestingGachas) {
                latestInterestingGachas.offer(new PetGachaLog(playerName, player.getPrefabId(), petInformations.getId(), timeProvider.currentTime()));
            }
        }

        Pet pet = petRepository.saveAndFlush(createPet(accountId, petInformations.getId()));

        eventPublisher.publishEvent(new PetGachaEvent(this, pet));

        return pet.toDetail();
    }

    public Collection<PetGachaLog> getLatestInterestingGachas() {
        synchronized (latestInterestingGachas) {
            return new ArrayList<>(latestInterestingGachas);
        }
    }

    public PetFusionResult fusion(long accountId, long petId, long subPetId) {
        Pet mainPet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.unknown("宠物不存在"));
        Pet subPet = petRepository.findByIdForWrite(subPetId).orElseThrow(() -> KxyWebException.unknown("宠物不存在"));
        mainPet.verifyOwner(accountId);
        subPet.verifyOwner(accountId);
        PetInformations subPetInformations = resourceContext.getLoader(PetInformations.class).get(subPet.getDefinitionId());
        if (mainPet.getRank() < 7) {
            throw KxyWebException.unknown("宠物未达到强化等级要求");
        }
        if (subPetInformations.getColor() != 3 && subPetInformations.getColor() != 4) {
            throw KxyWebException.unknown("材料宠物的品质不对");
        }
        if (playerRelationRepository.findById(accountId).get().toBattlePetIds().contains(subPetId)) {
            throw KxyWebException.unknown("材料宠物正在出战");
        }
        List<Long> mainPetAbilities = mainPet.getAbilities();
        List<Long> subPetAbilities = subPet.getAbilities();
        subPetAbilities.removeAll(mainPetAbilities);
        if (subPetAbilities.isEmpty()) {
            throw KxyWebException.unknown("材料宠物没有可用的技能");
        }

        ResourceLoader<PetAbilityInformation> abilityLoader = resourceContext.getLoader(PetAbilityInformation.class);
        petRepository.delete(subPet);
        RandomSelectorBuilder<Long> newAbilitySelectorBuilder = RandomSelector.builder();
        subPetAbilities.stream()
                .map(abilityLoader::get)
                .forEach(ability -> {
                    newAbilitySelectorBuilder.add(ability.getId(), ability.getMaterialPet());
                });
        long newAbility = newAbilitySelectorBuilder.build(RandomSelectType.DEPENDENT).getSingle();
        RandomSelectorBuilder<Long> droppedAbilitySelectorBuilder = RandomSelector.builder();
        mainPetAbilities.stream()
                .map(abilityLoader::get)
                .forEach(ability -> {
                    droppedAbilitySelectorBuilder.add(ability.getId(), ability.getMainPet());
                });
        long droppedAbility = droppedAbilitySelectorBuilder.build(RandomSelectType.DEPENDENT).getSingle();

        int index = mainPetAbilities.indexOf(droppedAbility);
        mainPetAbilities.set(index, newAbility);
        mainPet.importAbilities(mainPetAbilities);
        return new PetFusionResult(mainPet.toExtraDetail(playerRepository, resourceContext), newAbility, droppedAbility);
    }

    public Pet createForTest(long accountId, long definitionId) {
        return petRepository.saveAndFlush(createPet(accountId, definitionId));
    }

    public Pet createForNewComers(long accountId, long definitionId) {
        if (resourceContext.getLoader(PetInformations.class).get(definitionId).getColor() != 2) {
            return null;
        }
        return petRepository.saveAndFlush(createPet(accountId, definitionId));
    }

    public Pet createForRedeem(long accountId, long definitionId, Duration lock) {
        Pet pet = createPet(accountId, definitionId);
        pet.setNextWithdrawTime(Date.from(timeProvider.currentInstant().plus(lock)));
        return petRepository.saveAndFlush(pet);
    }

    public Pet createWithNumber(long accountId, long definitionId, int number) {
        Pet pet = createPet(accountId, definitionId);
        pet.setNumber(number);
        return petRepository.saveAndFlush(pet);
    }

    public PetDetail redeem(long accountId, long currencyId) {
        CurrencyToConsumables ctc = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 5)
                .filter(it -> it.getId() == currencyId)
                .findAny().orElse(null);
        if (ctc != null) {
            currencyService.decreaseCurrency(accountId, currencyId, 1);
            //
            long definitionId = ctc.getEffectParameter();
            Duration lock = ctc.getExtraID() == 1 ? Duration.ofDays(ctc.getExtraParameter()) : Duration.ofDays(3);
            int count = 0;
            while (definitionId < 300000 && count < 10) {
                definitionId = resourceContext.getLoader(PetCollection.class).get(definitionId).getOnePrototypeId();
                count++;
            }
            //
            return createForRedeem(accountId, definitionId, lock).toDetail();
        }
        return null;
    }

    public PetGachaAbilityResult gachaAbility(long accountId, long petId) {
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        pet.verifyOwner(accountId);

        int availableAbilityReserve = PetConstants.availableAbilityReserve(pet, resourceContext);
        if (availableAbilityReserve <= 0) {
            throw KxyWebException.unknown("没有空技能格");
        }
        int assumedAbilityCount = pet.getMaxAbilityCapacity() - availableAbilityReserve;
        PetAbilityStudy petAbilityStudy = resourceContext.getLoader(PetAbilityStudy.class).getAll().values().stream()
                .filter(it -> it.getAbilityAmount() == assumedAbilityCount)
                .findAny().orElseThrow(() -> KxyWebException.unknown("找不到技能学习配置，技能数：" + assumedAbilityCount));

        currencyService.decreaseCurrency(accountId, petAbilityStudy.getCurrencyId(), petAbilityStudy.getAmount());
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
        PetSkillCollections petSkillCollections = resourceContext.getLoader(PetSkillCollections.class).get(petInformations.getNormalSkillCollections());
        Random random = RandomProvider.getRandom();
        double probablity = petAbilityStudy.getSuccessRate();
        boolean success = random.nextDouble() < probablity;

        if (success) {
            List<Long> abilities = pet.getAbilities();
            List<Long> candidateAbilities = new ArrayList<>();
            for (int safeCount = 0; safeCount < 1000; safeCount++) {
                long abilityId = petSkillCollections.getSelector().getSingle().getId();
                if (abilities.contains(abilityId) || candidateAbilities.contains(abilityId)) {
                    continue;
                }
                candidateAbilities.add(abilityId);
                if (candidateAbilities.size() >= 3) {
                    break;
                }
            }
            pet.importCandidateAbilities(candidateAbilities);
        }

        return new PetGachaAbilityResult(pet, success);
    }

    public Pet acquireAbility(long accountId, long petId, long abilityId) {
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        pet.verifyOwner(accountId);
        List<Long> candidateAbilities = pet.getCandidateAbilities();
        if (!candidateAbilities.contains(abilityId)) {
            throw PetException.notInCandidate();
        }

        List<Long> abilities = pet.getAbilities();
        abilities.add(abilityId);
        pet.importAbilities(abilities);
        pet.setCandidateAbilitiesText("");
        //
        eventPublisher.publishEvent(new PetAbilityAcquiredEvent(this, pet, abilityId));
        //
        return pet;
    }

    public PetEnhanceResult enhance(long accountId, long petId) {
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        pet.verifyOwner(accountId);
        PetAddStar petAddStar = resourceContext.getByLoaderType(PetAddStarLoader.class).getByLevelAndStage(pet.getRank(), pet.getRankProgress());
        if (pet.getRank() >= pet.getMaxRank() || petAddStar.getAmount() == 0) {
            throw KxyWebException.unknown("强化已达到上限");
        }

        Long newAbilityId = null;
        currencyService.decreaseCurrency(accountId, petAddStar.getCurrencyId(), petAddStar.getAmount());
        boolean success = RandomProvider.getRandom().nextDouble() < petAddStar.getRate();
        if (success) {
            int rank = pet.getRank();
            int progress = pet.getRankProgress();
            progress++;
            if (progress >= 5) {
                rank++;
                progress = 0;
                Player player = playerRepository.findById(accountId).get();
                if (rank == 4) {
                    List<Long> abilities = pet.getAbilities();
                    PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
                    newAbilityId = resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(petInformations.getActivationSkillCollections()).get().iterator().next().getId();
                    for (int i = 0; i < 10000; i++) {
                        if (!abilities.contains(newAbilityId)) {
                            break;
                        }
                        newAbilityId = resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(petInformations.getActivationSkillCollections()).get().iterator().next().getId();
                    }
                    abilities.add(newAbilityId);
                    pet.importAbilities(abilities);
                } else if (rank == 7) {
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    PetConstants.BROADCAST_ID_RANK_7,
                                    ImmutableMap.of("playerName", player.getPlayerName(), "petName", pet.getPetName())
                            )
                    );
                } else if (rank == 10) {
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    PetConstants.BROADCAST_ID_RANK_10,
                                    ImmutableMap.of("playerName", player.getPlayerName(), "petName", pet.getPetName())
                            )
                    );
                }
            }
            pet.setRank(rank);
            pet.setRankProgress(progress);
        } else {
            pet.setRankProgress(0);
        }
        PetEnhanceResult result = new PetEnhanceResult(pet, success, newAbilityId);
        eventPublisher.publishEvent(new PetEnhancedEvent(this, result));
        return result;
    }

    public List<RecyclingResult> recycle(long accountId, List<Long> petIds) {
        if (petIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> battlePetIds = playerRelationRepository.findOrCreate(accountId).toBattlePetIds();
        List<Pet> pets = petRepository.findByIdsForWrite(petIds).stream()
                .filter(it -> !battlePetIds.contains(it.getId()))
                .filter(it -> it.getNftId() == null)
                .peek(it -> it.verifyOwner(accountId))
                .collect(Collectors.toList());

        RandomSelector<WasteRecovery> selector = resourceContext.getByLoaderType(WasteRecoveryLoader.class).getPetSelector();
        List<RecyclingResult> results = new ArrayList<>();
        pets.forEach(it -> {
            PetInformations petInfomations = resourceContext.getLoader(PetInformations.class).get(it.getDefinitionId());
            if (petInfomations.getColor() == 3 || petInfomations.getColor() == 4) {
                results.add(new RecyclingResult(it.getId(),
                        new CurrencyStack(CurrencyConstants.ID_魂晶, (petInfomations.getColor() == 3 ? PetConstants.RECYCLE_BLUE_PET_GET_195 : PetConstants.RECYCLE_PURPLE_PET_GET_195)),
                        true));
            } else {
                WasteRecovery wasteRecovery = selector.getSingle();
                results.add(new RecyclingResult(it.getId(), new CurrencyStack(wasteRecovery.getRecoveryId(), wasteRecovery.getAmount()), wasteRecovery.getHighRate() == 1));
            }
        });
        results.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getCurrencyStack().getCurrencyId(),
                        Collectors.mapping(it -> it.getCurrencyStack().getAmount(), Collectors.summingLong(Long::valueOf))
                ))
                .forEach((currencyId, amount) -> currencyService.increaseCurrency(accountId, currencyId, amount));
        petRepository.flush();
        petRepository.deleteInBatch(pets);

        return results;
    }

    public Pet soul(long accountId, long petId) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        pet.verifyOwner(accountId);
        //
        if (pet.getSoulLevel() + 1 >= resourceContext.getLoader(PetSoulLevel.class).getAll().size()) {
            throw PetException.soulLevelReachMax();
        }
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
        if (petInformations.getColor() < 4) {
            throw PetException.insufficientPetColor();
        }
        //
        PetSoulLevel petSoulLevel = resourceContext.getLoader(PetSoulLevel.class).get(pet.getSoulLevel());
        long nextLevelExp = petInformations.getColor() == 4 ? petSoulLevel.getPurple_exp() : petSoulLevel.getOrange_exp();
        long acturalCost = Math.min(currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_魂晶), nextLevelExp - pet.getSoulExp());
        if (acturalCost < 1) {
            throw PetException.insufficient195();
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_魂晶, acturalCost, true);
        pet.setSoulExp(pet.getSoulExp() + acturalCost);
        if (pet.getSoulExp() >= nextLevelExp) {
            pet.setSoulExp(pet.getSoulExp() - nextLevelExp);
            pet.setSoulLevel(pet.getSoulLevel() + 1);
            if (pet.getSoulLevel() >= 10) {
                Map<String, Object> args = new HashMap<>();
                args.put("playerName", player.getPlayerName());
                args.put("petName", petInformations.getPetName());
                args.put("soulLevel", pet.getSoulLevel());
                chatService.sendSystemMessage(
                        ChatConstants.SERVICE_ID_UNDIFINED,
                        ChatMessage.createTemplateMessage(
                                PetConstants.BROADCAST_ID_SOUL_LEVEL_OVER_10,
                                args
                        )
                );
            }
        }
        pet = petRepository.save(pet);
        //
        return pet;
    }

    public Pet wash(long accountId, long petId) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        Pet pet = petRepository.findByIdForWrite(petId).orElseThrow(() -> KxyWebException.notFound("宠物不存在"));
        pet.verifyOwner(accountId);
        //
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
        if (petInformations.getColor() < 4) {
            throw PetException.insufficientPetColor();
        }
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_元宝) < PetConstants.PRICE_WASH) {
            throw PetException.insufficientYB();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, PetConstants.PRICE_WASH, true, CurrencyConstants.PURPOSE_DECREMENT_宠物附魂);
        //
        pet.setSoulName_1(petSoulPartJackpot.get(0).getSingle());
        long soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_1(soulNameId);
        //
        pet.setSoulName_2(petSoulPartJackpot.get(1).getSingle());
        soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_2(soulNameId);
        //
        pet.setSoulName_3(petSoulPartJackpot.get(2).getSingle());
        soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_3(soulNameId);
        //
        pet.setSoulName_4(petSoulPartJackpot.get(3).getSingle());
        soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_4(soulNameId);
        //
        pet.setSoulName_5(petSoulPartJackpot.get(4).getSingle());
        soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_5(soulNameId);
        //
        pet.setSoulName_6(petSoulPartJackpot.get(5).getSingle());
        soulNameId = petSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(PetSoulName.class).getAll().size() && petInformations.getColor() < 5) {
            soulNameId -= 1;
        }
        pet.setSoulNameId_6(soulNameId);
        //
        if (Objects.equals(pet.getSoulNameId_1(), pet.getSoulNameId_2())
                && Objects.equals(pet.getSoulNameId_2(), pet.getSoulNameId_3())
                && Objects.equals(pet.getSoulNameId_3(), pet.getSoulNameId_4())
                && Objects.equals(pet.getSoulNameId_4(), pet.getSoulNameId_5())
                && Objects.equals(pet.getSoulNameId_5(), pet.getSoulNameId_6())
                && pet.getSoulNameId_6() == resourceContext.getLoader(PetSoulName.class).getAll().size()) {
            Map<String, Object> args = new HashMap<>();
            args.put("playerName", player.getPlayerName());
            args.put("petName", petInformations.getPetName());
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            PetConstants.BROADCAST_ID_SOUL_NAME_ID_BINGO,
                            args
                    )
            );
        }
        pet = petRepository.save(pet);
        //
        return pet;
    }

    public Pet createAndSavePetByPrototype(long accountId, long prototype) {
        Pet pet = createPetByPrototype(prototype);
        pet.setAccountId(accountId);
        return petRepository.saveAndFlush(pet);
    }

    public Pet createPetByPrototype(long prototype) {
        GodPet godPet = resourceContext.getLoader(GodPet.class).get(prototype);

        Pet pet = new Pet();
        pet.setDefinitionId(godPet.getPrototypeId());
        pet.setPetName(godPet.getPetName());
        pet.setRankProgress(godPet.getNowStarStage());
        pet.setRank(godPet.getNowStarLevel());
        pet.setMaxRank(godPet.getMaxStarLevel());
        pet.importAbilities(Collections.singletonList(resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(godPet.getInitialSkill()).get().iterator().next().getId()));

        pet.setAptitudeHp(godPet.get生命资质());
        pet.setAptitudeAtk(godPet.get攻击资质());
        pet.setAptitudePdef(godPet.get物防资质());
        pet.setAptitudeMdef(godPet.get法防资质());
        pet.setAptitudeSpd(godPet.get速度资质());
        pet.updateSortingIndex();

        pet.setNumber(godPet.getNowNumber());

        return pet;
    }

    private Pet createPet(long accountId, long definitionId) {
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(definitionId);
        StochasticModelLoader stochasticModelLoader = resourceContext.getByLoaderType(StochasticModelLoader.class);

        Pet pet = new Pet();
        pet.setAccountId(accountId);
        pet.setDefinitionId(definitionId);
        pet.setPetName(petInformations.getPetName());

        NormalRandomGenerator normalRandomGenerator = stochasticModelLoader.getNormalRandomGenerator(petInformations.getNormalStochasticModel());
        pet.setMaxRank(petInformations.generateMaxRank());
        pet.setMaxAbilityCapacity(petInformations.getMaxAbilityCount());

        List<Long> abilityIds = new ArrayList<>();
        abilityIds.add(resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(petInformations.getInitialSkillOne()).get().iterator().next().getId());
        Long initialSkillTwoId = resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(petInformations.getInitialSkillTwo()).get().iterator().next().getId();
        for (int i = 0; i < 10000; i++) {
            if (!abilityIds.contains(initialSkillTwoId)) {
                break;
            }
            initialSkillTwoId = resourceContext.getLoader(PetAbilityInformationCollectionSupplier.class).get(petInformations.getInitialSkillTwo()).get().iterator().next().getId();
        }
        abilityIds.add(initialSkillTwoId);
        pet.importAbilities(abilityIds);

        pet.setAptitudeHp(normalRandomGenerator.generateRanged(petInformations.get生命资质下限(), petInformations.get生命资质上限()));
        pet.setAptitudeAtk(normalRandomGenerator.generateRanged(petInformations.get攻击资质下限(), petInformations.get攻击资质上限()));
        pet.setAptitudePdef(normalRandomGenerator.generateRanged(petInformations.get物防资质下限(), petInformations.get物防资质上限()));
        pet.setAptitudeMdef(normalRandomGenerator.generateRanged(petInformations.get法防资质下限(), petInformations.get法防资质上限()));
        pet.setAptitudeSpd(normalRandomGenerator.generateRanged(petInformations.get速度资质下限(), petInformations.get速度资质上限()));
        pet.updateSortingIndex();

        return pet;
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        int referenceLevel = PetConstants.referenceLevel(accountId, playerRepository);
        PlayerRelation playerRelation = playerRelationRepository.findOrDummy(accountId);
        List<ParameterSpace> parameterSpaces = playerRelation.toBattlePetIds().stream()
                .map(id -> Optional.ofNullable(id)
                .flatMap(petRepository::findById)
                .map(pet -> pet.createFcParameterSpace(referenceLevel, resourceContext))
                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new AggregateParameterSpace(parameterSpaces);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder petSoulNameJackpotBuilder = RandomSelector.<Long>builder();
        List<PetSoulName> petSoulName = new ArrayList<>(resourceContext.getLoader(PetSoulName.class).getAll().values());
        petSoulName.forEach((psn) -> {
            petSoulNameJackpotBuilder.add(psn.getId(), psn.getProbability());
        });
        petSoulNameJackpot = petSoulNameJackpotBuilder.build(RandomSelectType.DEPENDENT);
        //
        petSoulPartJackpot = new ArrayList<>();
        List<PetSoulPart> petSoulPart = new ArrayList<>(resourceContext.getLoader(PetSoulPart.class).getAll().values());
        petSoulPart.forEach((psp) -> {
            RandomSelectorBuilder petSoulPartJackpotBuilder = RandomSelector.<Long>builder();
            petSoulPartJackpotBuilder.add(psp.getName_1(), psp.getProbability_1());
            petSoulPartJackpotBuilder.add(psp.getName_2(), psp.getProbability_2());
            petSoulPartJackpotBuilder.add(psp.getName_3(), psp.getProbability_3());
            petSoulPartJackpotBuilder.add(psp.getName_4(), psp.getProbability_4());
            petSoulPartJackpotBuilder.add(psp.getName_5(), psp.getProbability_5());
            petSoulPartJackpot.add(petSoulPartJackpotBuilder.build(RandomSelectType.DEPENDENT));
        });
    }

}
