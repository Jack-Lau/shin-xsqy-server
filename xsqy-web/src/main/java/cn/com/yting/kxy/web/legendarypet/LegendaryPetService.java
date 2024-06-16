/*
 * Created 2019-1-23 17:34:58
 */
package cn.com.yting.kxy.web.legendarypet;

import java.util.List;
import java.util.Map;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.legendarypet.resource.GoodPetAdvanced;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.pet.PetService;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class LegendaryPetService {

    @Autowired
    private LegendaryPetGenerationRepository legendaryPetGenerationRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PetService petService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private ResourceContext resourceContext;

    public LegendaryPetGenerationRecord findOrCreateGenerationRecord(long definitionId) {
        LegendaryPetGenerationRecord generationRecord = legendaryPetGenerationRepository.findByIdForWrite(definitionId).orElse(null);
        if (generationRecord == null) {
            generationRecord = new LegendaryPetGenerationRecord();
            generationRecord.setDefinitionId(definitionId);
            int A = 100;
            generationRecord.setAvailableCount(A);
            generationRecord = legendaryPetGenerationRepository.save(generationRecord);
        }
        return generationRecord;
    }

    public Pet redeem(long accountId, long lengendaryPetDefinitionId) {
        LegendaryPetGenerationRecord generationRecord = findOrCreateGenerationRecord(lengendaryPetDefinitionId);
        if (generationRecord.getAvailableCount() <= 0) {
            throw new LegendaryPetException(LegendaryPetException.EC_数量不足, "数量不足");
        }
        if (playerRepository.getOne(accountId).getPlayerLevel() < 80) {
            throw new LegendaryPetException(LegendaryPetException.EC_兑换所需角色等级不足, "兑换所需角色等级不足");
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_神兽精魄, 100);
        Pet pet = petService.createWithNumber(accountId, lengendaryPetDefinitionId, generationRecord.getSerialNumber() + 1);
        pet.setLegendary(true);
        generationRecord.increaseSerialNumber();
        generationRecord.decreaseAvailableCount();
        generationRecord.increaseRedeemedCount();
        chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(3200049, ImmutableMap.of(
                "playerName", playerRepository.findById(accountId).get().getPlayerName(),
                "petName", pet.getPetName()
        )));
        return pet;
    }

    public Pet ascend(long accountId, long petId) {
        if (playerRepository.getOne(accountId).getPlayerLevel() < 90) {
            throw new LegendaryPetException(LegendaryPetException.EC_进阶所需角色等级不足, "进阶所需角色等级不足");
        }
        //
        Pet pet = petRepository.findByIdForWrite(petId).get();
        pet.verifyOwner(accountId);
        GoodPetAdvanced goodPetAdvanced = resourceContext.getLoader(GoodPetAdvanced.class).get(pet.getDefinitionId());
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(goodPetAdvanced.getAdvancedId());
        goodPetAdvanced.getCosts().forEach(it -> currencyService.decreaseCurrency(accountId, it.getCurrencyId(), it.getAmount()));

        pet.setDefinitionId(goodPetAdvanced.getAdvancedId());
        pet.setMaxAbilityCapacity(petInformations.getMaxAbilityCount());
        pet.setAptitudeHp(pet.getAptitudeHp() + goodPetAdvanced.getPromoteAptitude());
        pet.setAptitudeAtk(pet.getAptitudeAtk() + goodPetAdvanced.getPromoteAptitude());
        pet.setAptitudePdef(pet.getAptitudePdef() + goodPetAdvanced.getPromoteAptitude());
        pet.setAptitudeMdef(pet.getAptitudeMdef() + goodPetAdvanced.getPromoteAptitude());
        pet.setAptitudeSpd(pet.getAptitudeSpd() + goodPetAdvanced.getPromoteAptitude());
        pet.updateSortingIndex();

        List<Long> abilities = pet.getAbilities();
        abilities.add(goodPetAdvanced.getGetAbility());
        pet.importAbilities(abilities);

        return pet;
    }

    public Pet redeemSpecial(long accountId, long currencyId) {
        Map<Long, Long> redeemMap = ImmutableMap.of(20050L, 300018L, 20051L, 300023L);
        if (!redeemMap.containsKey(currencyId)) {
            throw new LegendaryPetException(LegendaryPetException.EC_货币不正确, "货币不正确");
        }

        long targetLegendaryPetDefinitionId = redeemMap.get(currencyId);
        LegendaryPetGenerationRecord generationRecord = findOrCreateGenerationRecord(targetLegendaryPetDefinitionId);
        if (generationRecord.getAvailableCount() <= 0) {
            throw new LegendaryPetException(LegendaryPetException.EC_数量不足, "数量不足");
        }

        generationRecord.decreaseAvailableCount();
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        Pet pet = petService.createWithNumber(accountId, targetLegendaryPetDefinitionId, generationRecord.getSerialNumber() + 1);
        pet.setLegendary(true);
        generationRecord.increaseSerialNumber();

        return pet;
    }
}
