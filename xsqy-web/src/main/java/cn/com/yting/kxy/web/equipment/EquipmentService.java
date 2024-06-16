/*
 * Created 2018-9-17 16:01:14
 */
package cn.com.yting.kxy.web.equipment;

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
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.SimpleParameterBase;
import cn.com.yting.kxy.core.parameter.SingleElementParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.chat.model.TemplateTypes;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.equipment.resource.EquipmentCollection;
import cn.com.yting.kxy.web.equipment.resource.EquipmentEffect;
import cn.com.yting.kxy.web.equipment.resource.EquipmentEffectCollectionSupplier;
import cn.com.yting.kxy.web.equipment.resource.EquipmentProduce;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSchoolStrengthen;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSoulLevel;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSoulName;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSoulPart;
import cn.com.yting.kxy.web.equipment.resource.EquipmentSpeciallyEffect;
import cn.com.yting.kxy.web.equipment.resource.EquipmentStrengthening;
import cn.com.yting.kxy.web.equipment.resource.EquipmentStrengtheningStatus;
import cn.com.yting.kxy.web.equipment.resource.GodEquipment;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.price.PriceConstants;
import cn.com.yting.kxy.web.price.PriceService;
import cn.com.yting.kxy.web.recycling.RecyclingResult;
import cn.com.yting.kxy.web.recycling.resource.WasteRecovery;
import cn.com.yting.kxy.web.recycling.resource.WasteRecoveryLoader;
import cn.com.yting.kxy.web.school.SchoolRecord;
import cn.com.yting.kxy.web.school.SchoolRepository;
import com.google.common.collect.EvictingQueue;
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
public class EquipmentService implements InitializingBean, ParameterSpaceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentService.class);

    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final Queue<EquipmentForgingLog> latestInterestingForgings = EvictingQueue.create(10);

    private RandomSelector<Long> equipmentSoulNameJackpot;
    private Map<Integer, RandomSelector<String>> equipmentSoulPartJackpot;

    public void arm(long accountId, long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> KxyWebException.notFound("装备不存在"));
        equipment.verifyOwner(accountId);
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
        switch (equipmentProduce.getPart()) {
            case EquipmentConstants.PART_TYPE_HAND:
                playerRelation.setHandEquipmentId(equipmentId);
                break;
            case EquipmentConstants.PART_TYPE_BODY:
                playerRelation.setBodyEquipmentId(equipmentId);
                break;
            case EquipmentConstants.PART_TYPE_FOOT:
                playerRelation.setFootEquipmentId(equipmentId);
                break;
            case EquipmentConstants.PART_TYPE_HEAD:
                playerRelation.setHeadEquipmentId(equipmentId);
                break;
            case EquipmentConstants.PART_TYPE_NECK:
                playerRelation.setNeckEquipmentId(equipmentId);
                break;
            case EquipmentConstants.PART_TYPE_WAIST:
                playerRelation.setWaistEquipmentId(equipmentId);
                break;
        }
    }

    public void disarm(long accountId, int partType) {
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        switch (partType) {
            case EquipmentConstants.PART_TYPE_HAND:
                playerRelation.setHandEquipmentId(null);
                break;
            case EquipmentConstants.PART_TYPE_BODY:
                playerRelation.setBodyEquipmentId(null);
                break;
            case EquipmentConstants.PART_TYPE_FOOT:
                playerRelation.setFootEquipmentId(null);
                break;
            case EquipmentConstants.PART_TYPE_HEAD:
                playerRelation.setHeadEquipmentId(null);
                break;
            case EquipmentConstants.PART_TYPE_NECK:
                playerRelation.setNeckEquipmentId(null);
                break;
            case EquipmentConstants.PART_TYPE_WAIST:
                playerRelation.setWaistEquipmentId(null);
                break;
        }
    }

    public Equipment createForTest(long accountId, long definitionId) {
        return equipmentRepository.saveAndFlush(createEquipment(accountId, definitionId));
    }

    public Equipment createForNewComers(long accountId, long definitionId) {
        if (resourceContext.getLoader(EquipmentProduce.class).get(definitionId).getColor() != 2) {
            return null;
        }
        Equipment equipment = createEquipment(accountId, definitionId);
        equipment.setCreatorName("萌新专用★");
        return equipmentRepository.saveAndFlush(equipment);
    }

    public Equipment createForRedeem(long accountId, long definitionId, String source, Duration lock) {
        Equipment equipment = createEquipment(accountId, definitionId);
        equipment.setCreatorName(source + "★");
        equipment.setNextWithdrawTime(Date.from(timeProvider.currentInstant().plus(lock)));
        return equipmentRepository.saveAndFlush(equipment);
    }

    public Equipment redeem(long accountId, long currencyId) {
        CurrencyToConsumables ctc = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 4)
                .filter(it -> it.getId() == currencyId)
                .findAny().orElse(null);
        if (ctc != null) {
            currencyService.decreaseCurrency(accountId, currencyId, 1);
            long definitionId = ctc.getEffectParameter();
            Duration lock = ctc.getExtraID() == 1 ? Duration.ofDays(ctc.getExtraParameter()) : EquipmentConstants.DURATION_NEXT_WITHDRAW_TIME_FROM_REDEEM;
            return createForRedeem(accountId,
                    definitionId < 167000 ? definitionId : resourceContext.getLoader(EquipmentCollection.class).get(definitionId).getOnePrototypeId(),
                    resourceContext.getLoader(Currency.class).get(currencyId).getName(),
                    lock);
        }
        return null;
    }

    public Equipment forge(long accountId, long expectedPrice) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        if (player.getPlayerLevel() < EquipmentConstants.FORGE_REQUIRED_PLAYER_LEVEL) {
            LOG.info("未达到等级要求试图使用打造服务，accountId={}", accountId);
            throw KxyWebException.unknown("未达到等级");
        }
        String playerName = player.getPlayerName();
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, EquipmentConstants.PRICE_FORGE, true, CurrencyConstants.PURPOSE_DECREMENT_装备打造);
        priceService.deduct(accountId, PriceConstants.ID_装备打造, expectedPrice, CurrencyConstants.PURPOSE_DECREMENT_装备打造);

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
        List<EquipmentProduce> candidates = resourceContext.getLoader(EquipmentProduce.class).getAll().values().stream()
                .filter(it -> it.getColor() == color)
                .collect(Collectors.toList());
        if (candidates.isEmpty()) {
            throw new IllegalStateException("没有备选装备，color=" + color);
        }
        EquipmentProduce equipmentProduce = candidates.get(random.nextInt(candidates.size()));
        if (EquipmentConstants.DEFINITION_IDS_BINGO.contains(equipmentProduce.getId())) {
            Map<String, Object> args = new HashMap<>();
            args.put("playerName", playerName);
            TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            EquipmentConstants.BROADCAST_ID_FORGE_BINGO,
                            args
                    )
            );
        }
        if (color == 4 || color == 5) {
            synchronized (latestInterestingForgings) {
                latestInterestingForgings.offer(new EquipmentForgingLog(playerName, player.getPrefabId(), equipmentProduce.getId(), timeProvider.currentTime()));
            }
        }

        Equipment equipment = createEquipment(accountId, equipmentProduce.getId());
        equipment.setCreatorName(playerName);

        return equipmentRepository.saveAndFlush(equipment);
    }

    public Collection<EquipmentForgingLog> getLatestInterestingForgings() {
        synchronized (latestInterestingForgings) {
            return new ArrayList<>(latestInterestingForgings);
        }
    }

    public EnhancingResult enhance(long accountId, long equipmentId, boolean useInsurance) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        Equipment equipment = equipmentRepository.findByIdForWrite(equipmentId).orElseThrow(() -> KxyWebException.notFound("装备不存在"));
        equipment.verifyOwner(accountId);
        if (equipment.getEnhanceLevel() >= equipment.getMaxEnhanceLevel()) {
            throw EquipmentException.enhanceLevelReachMax();
        }
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
        EquipmentStrengthening equipmentStrengthening = resourceContext.getLoader(EquipmentStrengthening.class).get(equipment.getEnhanceLevel());
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_强化石);
        if (currencyRecord.getAmount() < equipmentStrengthening.getAmount()) {
            throw EquipmentException.insufficientCurrency();
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_强化石, equipmentStrengthening.getAmount());
        if (useInsurance) {
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_强化保护卡, 1);
        }
        EquipmentStrengtheningStatus status = equipmentStrengthening.getSelector().getSingle();
        Collection<EquipmentEffect> newEquipmentEffects = Collections.emptyList();
        switch (status) {
            case SUCCESSFUL:
                int beforeEnhanceLevel = equipment.getEnhanceLevel();
                int beforeHighestEnhanceLevelEver = equipment.getHighestEnhanceLevelEver();
                equipment.increaseEnhanceLevel();
                if (beforeEnhanceLevel == 3 && beforeHighestEnhanceLevelEver == 3) {
                    ResourceLoader<EquipmentEffectCollectionSupplier> loader = resourceContext.getLoader(EquipmentEffectCollectionSupplier.class);
                    EquipmentEffectCollectionSupplier equipmentEffectCollectionSupplier;
                    switch (equipmentProduce.getPart()) {
                        case EquipmentConstants.PART_TYPE_HAND:
                            equipmentEffectCollectionSupplier = loader.get(166000);
                            break;
                        case EquipmentConstants.PART_TYPE_FOOT:
                            equipmentEffectCollectionSupplier = loader.get(166003);
                            break;
                        case EquipmentConstants.PART_TYPE_BODY:
                            equipmentEffectCollectionSupplier = loader.get(166002);
                            break;
                        case EquipmentConstants.PART_TYPE_HEAD:
                            equipmentEffectCollectionSupplier = loader.get(166001);
                            break;
                        case EquipmentConstants.PART_TYPE_NECK:
                            equipmentEffectCollectionSupplier = loader.get(166005);
                            break;
                        case EquipmentConstants.PART_TYPE_WAIST:
                            equipmentEffectCollectionSupplier = loader.get(166004);
                            break;
                        default:
                            throw new AssertionError("不存在的部位");
                    }
                    newEquipmentEffects = equipmentEffectCollectionSupplier.get();
                    equipment.importAndMergeEffects(newEquipmentEffects);
                } else if (beforeEnhanceLevel == 6 && beforeHighestEnhanceLevelEver == 6) {
                    Map<String, Object> args = new HashMap<>();
                    args.put("playerName", player.getPlayerName());
                    TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    EquipmentConstants.BROADCAST_ID_ENHANCE_LV7,
                                    args
                            )
                    );
                } else if (beforeEnhanceLevel == 9 && beforeHighestEnhanceLevelEver == 9) {
                    ResourceLoader<EquipmentEffectCollectionSupplier> loader = resourceContext.getLoader(EquipmentEffectCollectionSupplier.class);
                    EquipmentEffectCollectionSupplier equipmentEffectCollectionSupplier;
                    switch (equipmentProduce.getPart()) {
                        case EquipmentConstants.PART_TYPE_HAND:
                            equipmentEffectCollectionSupplier = loader.get(166901);
                            break;
                        case EquipmentConstants.PART_TYPE_FOOT:
                            equipmentEffectCollectionSupplier = loader.get(166904);
                            break;
                        case EquipmentConstants.PART_TYPE_BODY:
                            equipmentEffectCollectionSupplier = loader.get(166903);
                            break;
                        case EquipmentConstants.PART_TYPE_HEAD:
                            equipmentEffectCollectionSupplier = loader.get(166902);
                            break;
                        case EquipmentConstants.PART_TYPE_NECK:
                            equipmentEffectCollectionSupplier = loader.get(166906);
                            break;
                        case EquipmentConstants.PART_TYPE_WAIST:
                            equipmentEffectCollectionSupplier = loader.get(166905);
                            break;
                        default:
                            throw new AssertionError("不存在的部位");
                    }
                    newEquipmentEffects = equipmentEffectCollectionSupplier.get();
                    equipment.importAndMergeEffects(newEquipmentEffects);

                    Map<String, Object> args = new HashMap<>();
                    args.put("playerName", player.getPlayerName());
                    TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    EquipmentConstants.BROADCAST_ID_ENHANCE_LV10,
                                    args
                            )
                    );
                }
                break;
            case UNCHANGED:
                // 无事发生
                break;
            case FAILED:
                if (!useInsurance) {
                    equipment.decreaseEnhanceLevel();
                }
                break;
            default:
                throw new AssertionError("Impossible");
        }

        EnhancingResult enhancingResult = new EnhancingResult(
                equipment.toDetail(resourceContext),
                newEquipmentEffects.stream()
                        .map(EquipmentEffect::getId)
                        .collect(Collectors.toList()),
                status
        );
        eventPublisher.publishEvent(new EquipmentEnhancedEvent(this, enhancingResult));
        return enhancingResult;
    }

    public FusionResult fusion(long accountId, long equipmentId, long subEquipmentId) {
        Equipment equipment = equipmentRepository.findByIdForWrite(equipmentId).orElseThrow(() -> KxyWebException.notFound("装备不存在"));
        Equipment subEquipment = equipmentRepository.findByIdForWrite(subEquipmentId).orElseThrow(() -> KxyWebException.unknown("装备不存在"));
        equipment.verifyOwner(accountId);
        subEquipment.verifyOwner(accountId);
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
        EquipmentProduce subEquipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(subEquipment.getDefinitionId());
        if (equipmentProduce.getPart() != subEquipmentProduce.getPart()) {
            throw KxyWebException.unknown("指定的装备不是相同部位");
        }
        if (equipment.getHighestEnhanceLevelEver() < 7) {
            throw KxyWebException.unknown("装备未达到强化等级要求");
        }
        if (subEquipmentProduce.getColor() != 3 && subEquipmentProduce.getColor() != 4) {
            throw KxyWebException.unknown("材料装备的品质不对");
        }
        List<Long> equipmentEffectIds = CommaSeparatedLists.fromText(equipment.getEffectsText(), Long::valueOf);
        List<Long> subEquipmentEffectIds = CommaSeparatedLists.fromText(subEquipment.getEffectsText(), Long::valueOf);
        subEquipmentEffectIds.removeAll(equipmentEffectIds);
        if (subEquipmentEffectIds.isEmpty()) {
            throw KxyWebException.unknown("材料装备没有可用的特效");
        }

        ResourceLoader<EquipmentEffect> effectLoader = resourceContext.getLoader(EquipmentEffect.class);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, EquipmentConstants.PRICE_FUSION, true, CurrencyConstants.PURPOSE_DECREMENT_装备重铸);
        equipmentRepository.delete(subEquipment);
        RandomSelectorBuilder<EquipmentEffect> builder = RandomSelector.builder();
        subEquipmentEffectIds.stream()
                .map(effectLoader::get)
                .filter(EquipmentSpeciallyEffect.class::isInstance)
                .map(EquipmentSpeciallyEffect.class::cast)
                .forEach(effect -> {
                    if (subEquipmentProduce.getColor() == 3) {
                        builder.add(effect, effect.getBlueRecast());
                    } else if (subEquipmentProduce.getColor() == 4) {
                        builder.add(effect, effect.getPurpleRecast());
                    }
                });
        Collection<EquipmentEffect> selectedEffects = builder.build(RandomSelectType.INDEPENDENT).get();
        if (selectedEffects.isEmpty()) {
            return new FusionResult(equipment.toDetail(resourceContext), false, Collections.emptyList(), Collections.emptyList());
        } else {
            List<EquipmentEffect> equipmentEffects = equipmentEffectIds.stream()
                    .map(effectLoader::get)
                    .collect(Collectors.toList());
            EquipmentEffect equipmentSchoolStrengthen = equipmentEffects.stream()
                    .filter(EquipmentSchoolStrengthen.class::isInstance)
                    .findAny().orElse(null);
            if (equipmentSchoolStrengthen != null) {
                equipmentEffects.remove(equipmentSchoolStrengthen);
            }
            List<EquipmentEffect> droppedEffects = Collections.emptyList();
            if (equipmentEffects.size() + selectedEffects.size() > EquipmentConstants.COUNT_MAX_EFFECTS) {
                Collections.shuffle(equipmentEffects);
                int bound = EquipmentConstants.COUNT_MAX_EFFECTS - selectedEffects.size();
                if (bound < 0) {
                    bound = 0;
                }
                droppedEffects = new ArrayList<>(equipmentEffects.subList(bound, equipmentEffects.size()));
                equipmentEffects = new ArrayList<>(equipmentEffects.subList(0, bound));
            }
            equipmentEffects.addAll(selectedEffects);
            if (equipmentSchoolStrengthen != null) {
                equipmentEffects.add(equipmentSchoolStrengthen);
            }
            equipment.importEffects(equipmentEffects);
            return new FusionResult(
                    equipment.toDetail(resourceContext),
                    true,
                    selectedEffects.stream().map(it -> it.getId()).collect(Collectors.toList()),
                    droppedEffects.stream().map(it -> it.getId()).collect(Collectors.toList())
            );
        }
    }

    public List<RecyclingResult> recycle(long accountId, List<Long> equipmentIds) {
        List<Long> armedEquipmentIds = playerRelationRepository.findOrCreate(accountId).toEquipmentIds();
        if (equipmentIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Equipment> equipments = equipmentRepository.findByIdsForWrite(equipmentIds).stream()
                .filter(it -> !armedEquipmentIds.contains(it.getId()))
                .filter(it -> it.getNftId() == null)
                .peek(it -> it.verifyOwner(accountId))
                .collect(Collectors.toList());

        RandomSelector<WasteRecovery> selector = resourceContext.getByLoaderType(WasteRecoveryLoader.class).getEquipmentSelector();
        List<RecyclingResult> results = new ArrayList<>();
        equipments.forEach(it -> {
            EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(it.getDefinitionId());
            if (equipmentProduce.getColor() == 3 || equipmentProduce.getColor() == 4) {
                results.add(new RecyclingResult(it.getId(),
                        new CurrencyStack(CurrencyConstants.ID_魂晶, (equipmentProduce.getColor() == 3 ? EquipmentConstants.RECYCLE_BLUE_EQUIPMENT_GET_195 : EquipmentConstants.RECYCLE_PURPLE_EQUIPMENT_GET_195)),
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
        equipmentRepository.flush();
        equipmentRepository.deleteAll(equipments);

        return results;
    }

    public Equipment soul(long accountId, long equipmentId) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        Equipment equipment = equipmentRepository.findByIdForWrite(equipmentId).orElseThrow(() -> KxyWebException.notFound("装备不存在"));
        equipment.verifyOwner(accountId);
        //
        if (equipment.getSoulLevel() + 1 >= resourceContext.getLoader(EquipmentSoulLevel.class).getAll().size()) {
            throw EquipmentException.soulLevelReachMax();
        }
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
        if (equipmentProduce.getColor() < 4) {
            throw EquipmentException.insufficientEquipmentColor();
        }
        //
        EquipmentSoulLevel equipmentSoulLevel = resourceContext.getLoader(EquipmentSoulLevel.class).get(equipment.getSoulLevel());
        long nextLevelExp = equipmentProduce.getColor() == 4 ? equipmentSoulLevel.getPurple_exp() : equipmentSoulLevel.getOrange_exp();
        long acturalCost = Math.min(currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_魂晶), nextLevelExp - equipment.getSoulExp());
        if (acturalCost < 1) {
            throw EquipmentException.insufficient195();
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_魂晶, acturalCost, true);
        equipment.setSoulExp(equipment.getSoulExp() + acturalCost);
        if (equipment.getSoulExp() >= nextLevelExp) {
            equipment.setSoulExp(equipment.getSoulExp() - nextLevelExp);
            equipment.setSoulLevel(equipment.getSoulLevel() + 1);
            if (equipment.getSoulLevel() >= 10) {
                Map<String, Object> args = new HashMap<>();
                args.put("playerName", player.getPlayerName());
                args.put("soulLevel", equipment.getSoulLevel());
                TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
                chatService.sendSystemMessage(
                        ChatConstants.SERVICE_ID_UNDIFINED,
                        ChatMessage.createTemplateMessage(
                                EquipmentConstants.BROADCAST_ID_SOUL_LEVEL_OVER_10,
                                args
                        )
                );
            }
        }
        equipment = equipmentRepository.save(equipment);
        //
        return equipment;
    }

    public Equipment wash(long accountId, long equipmentId) {
        Player player = playerRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("角色不存在"));
        Equipment equipment = equipmentRepository.findByIdForWrite(equipmentId).orElseThrow(() -> KxyWebException.notFound("装备不存在"));
        equipment.verifyOwner(accountId);
        //
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
        if (equipmentProduce.getColor() < 4) {
            throw EquipmentException.insufficientEquipmentColor();
        }
        if (!equipmentSoulPartJackpot.containsKey(equipmentProduce.getPart())) {
            throw EquipmentException.cannotSoulThisPart();
        }
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_元宝) < EquipmentConstants.PRICE_WASH) {
            throw EquipmentException.insufficientYB();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, EquipmentConstants.PRICE_WASH, true, CurrencyConstants.PURPOSE_DECREMENT_装备附魂);
        //
        equipment.setSoulName_1(equipmentSoulPartJackpot.get(equipmentProduce.getPart()).getSingle());
        long soulNameId = equipmentSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(EquipmentSoulName.class).getAll().size() && equipmentProduce.getColor() < 5) {
            soulNameId -= 1;
        }
        equipment.setSoulNameId_1(soulNameId);
        //
        equipment.setSoulName_2(equipmentSoulPartJackpot.get(equipmentProduce.getPart()).getSingle());
        soulNameId = equipmentSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(EquipmentSoulName.class).getAll().size() && equipmentProduce.getColor() < 5) {
            soulNameId -= 1;
        }
        equipment.setSoulNameId_2(soulNameId);
        //
        equipment.setSoulName_3(equipmentSoulPartJackpot.get(equipmentProduce.getPart()).getSingle());
        soulNameId = equipmentSoulNameJackpot.getSingle();
        if (soulNameId == resourceContext.getLoader(EquipmentSoulName.class).getAll().size() && equipmentProduce.getColor() < 5) {
            soulNameId -= 1;
        }
        equipment.setSoulNameId_3(soulNameId);
        //
        if (equipment.getSoulName_1().equals(equipment.getSoulName_2())
                && equipment.getSoulName_2().equals(equipment.getSoulName_3())) {
            Map<String, Object> args = new HashMap<>();
            args.put("playerName", player.getPlayerName());
            args.put("soulName", equipment.getSoulName_1());
            TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            EquipmentConstants.BROADCAST_ID_SOUL_NAME_BINGO,
                            args
                    )
            );
        }
        if (Objects.equals(equipment.getSoulNameId_1(), equipment.getSoulNameId_2())
                && Objects.equals(equipment.getSoulNameId_2(), equipment.getSoulNameId_3())
                && equipment.getSoulNameId_3() == resourceContext.getLoader(EquipmentSoulName.class).getAll().size()) {
            Map<String, Object> args = new HashMap<>();
            args.put("playerName", player.getPlayerName());
            TemplateTypes.EquipmentName.addTo(args, "equipment", equipmentProduce.getId(), player.getPrefabId());
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            EquipmentConstants.BROADCAST_ID_SOUL_NAME_ID_BINGO,
                            args
                    )
            );
        }
        equipment = equipmentRepository.save(equipment);
        //
        return equipment;
    }

    public Equipment createAndSaveEquipmentByPrototype(long accountId, long prototypeId) {
        Equipment equipment = createEquipmentByPrototype(prototypeId);
        equipment.setAccountId(accountId);
        return equipmentRepository.saveAndFlush(equipment);
    }

    public Equipment createEquipmentByPrototype(long prototypeId) {
        GodEquipment godEquipment = resourceContext.getLoader(GodEquipment.class).get(prototypeId);
        Equipment equipment = new Equipment();
        equipment.setDefinitionId(godEquipment.getPrototypeId());
        equipment.importBaseParameters(godEquipment.getBaseParameters());
        equipment.setEnhanceLevel(godEquipment.getNowEnhancementLevel());
        equipment.setMaxEnhanceLevel(godEquipment.getUpperLimitEnhancementLevel());
        equipment.importAndMergeEffectIds(godEquipment.getEffectIds());
        equipment.setNumber(godEquipment.getNowNumber());
        return equipment;
    }

    private Equipment createEquipment(long accountId, long definitionId) {
        EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(definitionId);
        List<Parameter> baseParameters = equipmentProduce.generateParameters();
        Equipment equipment = new Equipment();
        equipment.setAccountId(accountId);
        equipment.setDefinitionId(definitionId);
        equipment.importBaseParameters(baseParameters);
        equipment.setMaxEnhanceLevel(equipmentProduce.generateMaxEnhanceLevel());
        return equipment;
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        PlayerRelation playerRelation = playerRelationRepository.findById(accountId).orElse(null);
        if (playerRelation == null) {
            return ParameterSpace.EMPTY;
        } else {
            List<ParameterSpace> parameterSpaces = playerRelation.toEquipmentIds().stream()
                    .filter(Objects::nonNull)
                    .map(id -> equipmentRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .map(it -> it.createParameterSpace(resourceContext))
                    .collect(Collectors.toList());
            //处理装备的门派加成特效
            Optional<SchoolRecord> schoolRecord = schoolRepository.findById(accountId);
            if (schoolRecord.isPresent()) {
                List<ParameterSpace> extraParameterSpaces = new ArrayList<>();
                for (ParameterSpace ps : parameterSpaces) {
                    if (ps.getParameterBase(ParameterNameConstants.藏龙剑气强化).exportValue() > 0) {
                        if (schoolRecord.get().getSchoolId() == 101) {
                            extraParameterSpaces.add(new SingleElementParameterSpace(ParameterNameConstants.暴击率, new SimpleParameterBase(0.05)));
                        }
                    }
                    if (ps.getParameterBase(ParameterNameConstants.勾魂丝强化).exportValue() > 0) {
                        if (schoolRecord.get().getSchoolId() == 103) {
                            extraParameterSpaces.add(new SingleElementParameterSpace(ParameterNameConstants.吸血率, new SimpleParameterBase(0.05)));
                        }
                    }
                    if (ps.getParameterBase(ParameterNameConstants.上善若水强化).exportValue() > 0) {
                        if (schoolRecord.get().getSchoolId() == 104) {
                            extraParameterSpaces.add(new SingleElementParameterSpace(ParameterNameConstants.连击率, new SimpleParameterBase(0.05)));
                        }
                    }
                }
                parameterSpaces.addAll(extraParameterSpaces);
            }
            return new AggregateParameterSpace(parameterSpaces);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder equipmentSoulNameJackpotBuilder = RandomSelector.<Long>builder();
        List<EquipmentSoulName> equipmentSoulName = new ArrayList<>(resourceContext.getLoader(EquipmentSoulName.class).getAll().values());
        equipmentSoulName.forEach((esn) -> {
            equipmentSoulNameJackpotBuilder.add(esn.getId(), esn.getProbability());
        });
        equipmentSoulNameJackpot = equipmentSoulNameJackpotBuilder.build(RandomSelectType.DEPENDENT);
        //
        equipmentSoulPartJackpot = new HashMap<>();
        List<EquipmentSoulPart> equipmentSoulPart = new ArrayList<>(resourceContext.getLoader(EquipmentSoulPart.class).getAll().values());
        equipmentSoulPart.forEach((esp) -> {
            RandomSelectorBuilder equipmentSoulPartJackpotBuilder = RandomSelector.<Long>builder();
            equipmentSoulPartJackpotBuilder.add(esp.getName_1(), esp.getProbability_1());
            equipmentSoulPartJackpotBuilder.add(esp.getName_2(), esp.getProbability_2());
            equipmentSoulPartJackpotBuilder.add(esp.getName_3(), esp.getProbability_3());
            equipmentSoulPartJackpotBuilder.add(esp.getName_4(), esp.getProbability_4());
            equipmentSoulPartJackpotBuilder.add(esp.getName_5(), esp.getProbability_5());
            equipmentSoulPartJackpot.put(esp.getPart(), equipmentSoulPartJackpotBuilder.build(RandomSelectType.DEPENDENT));
        });
    }

}
