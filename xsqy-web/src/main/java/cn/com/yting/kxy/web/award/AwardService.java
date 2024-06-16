/*
 * Created 2018-8-3 17:22:10
 */
package cn.com.yting.kxy.web.award;

import java.util.ArrayList;
import java.util.List;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.award.model.Award;
import cn.com.yting.kxy.web.award.resource.Awards;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentService;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
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
public class AwardService {

    @Autowired
    private CompositePlayerService compositePlayerService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private PetService petService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public AwardResult processAward(long accountId, long awardId) {
        return processAward(accountId, awardId, CurrencyConstants.PURPOSE_INCREMENT_任务产生的奖励);
    }

    public AwardResult processAward(long accountId, long awardId, Integer purpose) {
        int playerLevel = playerRepository.findById(accountId).map(Player::getPlayerLevel).orElse(0);
        long playerFc = playerRepository.findById(accountId).map(Player::getFc).orElse(0L);
        Awards awards = Awards.getFrom(resourceContext, awardId);
        Award award = awards.createAward(playerLevel, playerFc);
        return processAward(accountId, award, purpose);
    }

    public AwardResult processAward(long accountId, List<Long> awardIds, Integer purpose) {
        int playerLevel = playerRepository.findById(accountId).map(Player::getPlayerLevel).orElse(0);
        long playerFc = playerRepository.findById(accountId).map(Player::getFc).orElse(0L);
        Award totalAward = Award.empty();
        for (long awardId : awardIds) {
            Awards awards = Awards.getFrom(resourceContext, awardId);
            Award award = awards.createAward(playerLevel, playerFc);
            totalAward = totalAward.plus(award);
        }
        return processAward(accountId, totalAward, purpose);
    }

    private AwardResult processAward(long accountId, Award award, Integer purpose) {
        List<CurrencyStack> currencyStacks = new ArrayList<>();
        List<Equipment> equipments = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_经验, award.getExp());
        currencyStacks.add(new CurrencyStack(CurrencyConstants.ID_经验, award.getExp()));

        award.getCurrencyChanceMap().forEach((currencyId, amount) -> {
            currencyService.increaseCurrency(accountId, currencyId, amount, purpose);
            currencyStacks.add(new CurrencyStack(currencyId, amount));
        });

        award.getEquipmentPrototypeIds().forEach(id -> {
            equipments.add(equipmentService.createForNewComers(accountId, id));
        });

        award.getPetPrototypeIds().forEach((id) -> {
            pets.add(petService.createForNewComers(accountId, id));
        });

        String playerName = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName();
        award.getBroadcasts().forEach(id -> {
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            id,
                            ImmutableMap.of(
                                    "playerName", playerName
                            )
                    )
            );
        });

        AwardResult result = new AwardResult(currencyStacks, equipments, pets);
        applicationEventPublisher.publishEvent(new AwardEvent(this, accountId, result));

        return result;
    }

    public AwardResult redeemAward(long accountId, long currencyId) {
        CurrencyToConsumables ctc = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 2)
                .filter(it -> it.getId() == currencyId)
                .findAny().orElse(null);
        if (ctc == null || ctc.getEffectParameter() == 0) {
            throw KxyWebException.unknown("不能用指定的货币兑换奖励");
        }
        //
        if (ctc.getConditionID() == 1) {
            if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_毫仙石) < ctc.getConditionParameter()) {
                throw AwardException.insufficientXS();
            }
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, ctc.getConditionParameter(), true, CurrencyConstants.PURPOSE_DECREMENT_使用消耗品);
        }
        if (ctc.getConditionID() == 2) {
            if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_元宝) < ctc.getConditionParameter()) {
                throw AwardException.insufficientYB();
            }
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, ctc.getConditionParameter(), true, CurrencyConstants.PURPOSE_DECREMENT_使用消耗品);
        }
        //
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        return processAward(accountId, ctc.getEffectParameter(), CurrencyConstants.PURPOSE_INCREMENT_使用消耗品);
    }

}
