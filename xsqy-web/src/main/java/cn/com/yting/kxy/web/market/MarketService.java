/*
 * Created 2018-12-19 16:42:15
 */
package cn.com.yting.kxy.web.market;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.KxyWebConstants;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentConstants;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.equipment.resource.EquipmentProduce;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.title.Title;
import cn.com.yting.kxy.web.title.TitleRepository;
import cn.com.yting.kxy.web.title.resource.TitleInformations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class MarketService {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private ConsignmentRepository consignmentRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private ConsignmentMarkerRepository consignmentMarkerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MailService mailService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;

    public Consignment createConsignment(long accountId, GoodsType goodsType, long goodsObjectId, long price) {
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 60) {
            throw new MarketException(MarketException.EC_等级不足, "等级不足");
        }
        if (player.getFc() < 30000) {
            throw new MarketException(MarketException.EC_战斗力不足, "战斗力不足");
        }
        if (consignmentRepository.countOnSaleBySellerAccountId(accountId, new Date(timeProvider.currentTime())) >= 8) {
            throw new MarketException(MarketException.EC_货品数量已达上限, "货品数量已达上限");
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, MarketConstants.CONSIGNMENT_FEE);

        PlayerRelation playerRelation = playerRelationRepository.findById(accountId).get();

        Consignment consignment = new Consignment();
        switch (goodsType) {
            case EQUIPMENT: {
                if (playerRelation.toEquipmentIds().contains(goodsObjectId)) {
                    throw new MarketException(MarketException.EC_装备正在装着中, "装备正在装着中");
                }
                Equipment equipment = equipmentRepository.findByIdForWrite(goodsObjectId).orElseThrow(() -> KxyWebException.unknown("装备不存在"));
                equipment.verifyOwner(accountId);
                if (equipment.getNextWithdrawTime() != null && equipment.getNextWithdrawTime().getTime() > timeProvider.currentTime()) {
                    throw new MarketException(MarketException.EC_装备交易冷却时间未到, "装备交易冷却时间未到");
                }
                EquipmentProduce equipmentProduce = resourceContext.getLoader(EquipmentProduce.class).get(equipment.getDefinitionId());
                if (equipmentProduce.getColor() == 2) {
                    throw new MarketException(MarketException.EC_装备的品质不对, "装备的品质不对");
                }
                equipment.setAccountId(KxyWebConstants.ACCOUNT_ID_NO_OWNER);
                consignment.setGoodsDefinitionId(equipment.getDefinitionId());

                ConsignmentEquipmentInfo consignmentEquipmentInfo = new ConsignmentEquipmentInfo();
                consignmentEquipmentInfo.setConsignment(consignment);
                consignmentEquipmentInfo.setPart(equipmentProduce.getPart());
                consignmentEquipmentInfo.setColor(equipmentProduce.getColor());
                ParameterSpace parameterSpace = equipment.createParameterSpace(resourceContext);
                consignmentEquipmentInfo.setPatk((int) parameterSpace.getParameterBase(ParameterNameConstants.物伤).exportValue());
                consignmentEquipmentInfo.setMatk((int) parameterSpace.getParameterBase(ParameterNameConstants.法伤).exportValue());
                consignmentEquipmentInfo.setFc((long) parameterSpace.getParameterBase(ParameterNameConstants.战斗力).exportValue());
                consignmentEquipmentInfo.setMaxEnhanceLevel(equipment.getMaxEnhanceLevel());
                consignment.setConsignmentEquipmentInfo(consignmentEquipmentInfo);

                List<ConsignmentEquipmentEffect> effects = CommaSeparatedLists.fromText(equipment.getEffectsText(), Long::valueOf).stream()
                        .map(effectId -> {
                            ConsignmentEquipmentEffect effect = new ConsignmentEquipmentEffect();
                            effect.setConsignment(consignment);
                            effect.setEffectId(effectId);
                            return effect;
                        })
                        .collect(Collectors.toList());
                consignment.setConsignmentEquipmentEffects(effects);

                break;
            }
            case PET: {
                if (playerRelation.toBattlePetIds().contains(goodsObjectId)) {
                    throw new MarketException(MarketException.EC_宠物正在出战中, "宠物正在出战中");
                }
                Pet pet = petRepository.findByIdForWrite(goodsObjectId).orElseThrow(() -> KxyWebException.unknown("宠物不存在"));
                pet.verifyOwner(accountId);
                if (pet.getNextWithdrawTime() != null && pet.getNextWithdrawTime().getTime() > timeProvider.currentTime()) {
                    throw new MarketException(MarketException.EC_宠物交易冷却时间未到, "宠物交易冷却时间未到");
                }
                PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
                if (petInformations.getColor() == 2) {
                    throw new MarketException(MarketException.EC_宠物的品质不对, "宠物的品质不对");
                }
                pet.setAccountId(KxyWebConstants.ACCOUNT_ID_NO_OWNER);
                consignment.setGoodsDefinitionId(pet.getDefinitionId());

                ConsignmentPetInfo consignmentPetInfo = new ConsignmentPetInfo();
                consignmentPetInfo.setConsignment(consignment);
                consignmentPetInfo.setAptitudeAtk(pet.getAptitudeAtk());
                consignmentPetInfo.setAptitudeHp(pet.getAptitudeHp());
                consignmentPetInfo.setAptitudeMdef(pet.getAptitudeMdef());
                consignmentPetInfo.setAptitudePdef(pet.getAptitudePdef());
                consignmentPetInfo.setAptitudeSpd(pet.getAptitudeSpd());
                consignmentPetInfo.setPetRank(pet.getRank());
                consignmentPetInfo.setMaxPetRank(pet.getMaxRank());
                consignment.setConsignmentPetInfo(consignmentPetInfo);

                List<ConsignmentPetAbility> abilities = pet.getAbilities().stream()
                        .map(abilityId -> {
                            ConsignmentPetAbility petAbility = new ConsignmentPetAbility();
                            petAbility.setConsignment(consignment);
                            petAbility.setAbilityId(abilityId);
                            return petAbility;
                        })
                        .collect(Collectors.toList());
                consignment.setConsignmentPetAbilities(abilities);

                break;
            }
            case TITLE: {
                if (Objects.equals(playerRelation.getTitleId(), goodsObjectId)) {
                    throw new MarketException(MarketException.EC_称号正在使用中, "称号正在使用中");
                }
                Title title = titleRepository.findByIdForWrite(goodsObjectId).orElseThrow(() -> KxyWebException.unknown("称号不存在"));
                title.verifyOwner(accountId);
                TitleInformations titleInformations = resourceContext.getLoader(TitleInformations.class).get(title.getDefinitionId());
                if (titleInformations.getType() != 1) {
                    throw new MarketException(MarketException.EC_称号类型不对, "称号类型不对");
                }
                if (title.getTradeLockTime() != null && title.getTradeLockTime().getTime() > timeProvider.currentTime()) {
                    throw new MarketException(MarketException.EC_称号的交易冷却时间未到, "称号的交易冷却时间未到");
                }
                title.setAccountId(KxyWebConstants.ACCOUNT_ID_NO_OWNER);
                consignment.setGoodsDefinitionId(title.getDefinitionId());
                break;
            }
        }

        consignment.setSellerAccountId(accountId);
        consignment.setGoodsType(goodsType);
        consignment.setGoodsObjectId(goodsObjectId);
        consignment.setPrice(price);
        Instant currentTime = timeProvider.currentInstant();
        consignment.setCreateTime(Date.from(currentTime));
        consignment.setDeadline(Date.from(currentTime.plus(MarketConstants.DURATION_SELLING)));

        return consignmentRepository.save(consignment);
    }

    public Consignment suspendConsignment(long accountId, long consignmentId) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        consignment.verifySeller(accountId);
        Date currentTime = new Date(timeProvider.currentTime());
        if (!consignment.isOnSale(currentTime)) {
            throw new MarketException(MarketException.EC_货品不是上架中状态, "货品不是上架中状态");
        }
        consignment.setCreateTime(null);
        consignment.setDeadline(null);
        consignment.setPreviousPrice(consignment.getPrice());

        return consignment;
    }

    public Consignment resumeConsignment(long accountId, long consignmentId, long price) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        consignment.verifySeller(accountId);
        Instant currentTime = timeProvider.currentInstant();
        if (consignment.isSold() || consignment.isOnSale(Date.from(currentTime))) {
            throw new MarketException(MarketException.EC_货品不是已下架状态, "货品不是已下架状态");
        }
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 50) {
            throw new MarketException(MarketException.EC_等级不足, "等级不足");
        }
        if (player.getFc() < 20000) {
            throw new MarketException(MarketException.EC_战斗力不足, "战斗力不足");
        }
        if (consignmentRepository.countOnSaleBySellerAccountId(accountId, new Date(timeProvider.currentTime())) >= 8) {
            throw new MarketException(MarketException.EC_货品数量已达上限, "货品数量已达上限");
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, MarketConstants.CONSIGNMENT_FEE);

        consignment.setPrice(price);
        consignment.setCreateTime(Date.from(currentTime));
        consignment.setDeadline(Date.from(currentTime.plus(MarketConstants.DURATION_SELLING)));

        return consignment;
    }

    public void cancelConsignment(long accountId, long consignmentId) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        consignment.verifySeller(accountId);
        Instant currentTime = timeProvider.currentInstant();
        if (consignment.isSold() || consignment.isOnSale(Date.from(currentTime))) {
            throw new MarketException(MarketException.EC_货品不是已下架状态, "货品不是已下架状态");
        }

        switch (consignment.getGoodsType()) {
            case EQUIPMENT: {
                Equipment equipment = equipmentRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("装备不存在"));
                equipment.setAccountId(accountId);
                break;
            }
            case PET: {
                Pet pet = petRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("宠物不存在"));
                pet.setAccountId(accountId);
                break;
            }
            case TITLE: {
                Title title = titleRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("称号不存在"));
                title.setAccountId(accountId);
                break;
            }
        }
        consignmentRepository.delete(consignment);
    }

    public ConsignmentMarker mark(long accountId, long consignmentId) {
        ConsignmentMarker marker = consignmentMarkerRepository.findById(accountId, consignmentId).orElse(null);
        if (marker != null) {
            return marker;
        }
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        if (consignment.getSellerAccountId() == accountId) {
            throw new MarketException(MarketException.EC_不能关注自己的货品, "不能关注自己的货品");
        }
        marker = new ConsignmentMarker();
        marker.setAccountId(accountId);
        marker.setConsignment(consignment);
        consignment.getMarkers().add(marker);
        return marker;
    }

    public void unmark(long accountId, long consignmentId) {
        ConsignmentMarker marker = consignmentMarkerRepository.findById(accountId, consignmentId).orElse(null);
        if (marker != null) {
            consignmentMarkerRepository.delete(marker);
        }
    }

    public Consignment purchase(long accountId, long consignmentId) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        Instant currentTime = timeProvider.currentInstant();
        if (!consignment.isOnSale(Date.from(currentTime))) {
            throw new MarketException(MarketException.EC_货品不是上架中状态, "货品不是上架中状态");
        }
        if (consignment.getSellerAccountId() == accountId) {
            throw new MarketException(MarketException.EC_不能购买自己的货品, "不能购买自己的货品");
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, consignment.getPrice(), true, CurrencyConstants.PURPOSE_DECREMENT_支付交易行货品);

        consignment.setSold(true);
        consignment.setBuyerAccountId(accountId);
        consignment.setDealTime(Date.from(currentTime));
        consignment.setConsignmentEquipmentInfo(null);
        consignment.getConsignmentEquipmentEffects().clear();
        consignment.setConsignmentPetInfo(null);
        consignment.getConsignmentPetAbilities().clear();

        MailSendingRequest.create()
                .to(consignment.getSellerAccountId())
                .template(54)
                .commit(mailService);

        return consignment;
    }

    public Consignment obtainPayment(long accountId, long consignmentId) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        consignment.verifySeller(accountId);
        if (!consignment.isSold()) {
            throw new MarketException(MarketException.EC_货品不是已售出状态, "货品不是已售出状态");
        }
        if (consignment.isPaymentDelivered()) {
            throw new MarketException(MarketException.EC_货款已经领取过, "货款已经领取过");
        }
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_毫仙石, consignment.getPrice(), CurrencyConstants.PURPOSE_INCREMENT_交易行出售货款);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, consignment.getPrice() / 10, true, CurrencyConstants.PURPOSE_DECREMENT_交易行手续费);
        consignment.setPaymentDelivered(true);
        return consignment;
    }

    public Consignment obtainGoods(long accountId, long consignmentId) {
        Consignment consignment = consignmentRepository.findByIdForWrite(consignmentId).orElseThrow(() -> KxyWebException.notFound("货品不存在"));
        if (!Objects.equals(consignment.getBuyerAccountId(), accountId)) {
            throw new MarketException(MarketException.EC_不是货品的购买者, "不是货品的购买者");
        }
        if (!consignment.isSold()) {
            throw new MarketException(MarketException.EC_货品不是已售出状态, "货品不是已售出状态");
        }
        if (consignment.isGoodsDelivered()) {
            throw new MarketException(MarketException.EC_货品已经领取过, "货品已经领取过");
        }

        switch (consignment.getGoodsType()) {
            case EQUIPMENT: {
                Equipment equipment = equipmentRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("装备不存在"));
                equipment.setAccountId(accountId);
                equipment.setNextWithdrawTime(Date.from(timeProvider.currentInstant().plus(EquipmentConstants.DURATION_NEXT_WITHDRAW_TIME_FROM_MARKET)));
                break;
            }
            case PET: {
                Pet pet = petRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("宠物不存在"));
                pet.setAccountId(accountId);
                pet.setNextWithdrawTime(Date.from(timeProvider.currentInstant().plus(EquipmentConstants.DURATION_NEXT_WITHDRAW_TIME_FROM_MARKET)));
                break;
            }
            case TITLE: {
                Title title = titleRepository.findByIdForWrite(consignment.getGoodsObjectId()).orElseThrow(() -> KxyWebException.unknown("称号不存在"));
                title.setAccountId(accountId);
                title.setTradeLockTime(Date.from(timeProvider.currentInstant().plus(EquipmentConstants.DURATION_NEXT_WITHDRAW_TIME_FROM_MARKET)));
                break;
            }
        }
        consignment.setGoodsDelivered(true);
        return consignment;
    }

    public ConsignmentDetail consignmentToDetail(Consignment consignment) {
        return consignment.toDetail();
    }
}
