/*
 * Created 2018-10-11 15:39:34
 */
package cn.com.yting.kxy.web.pet;

import java.util.List;
import java.util.Map;

import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebConstants;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Azige
 */
public final class PetConstants {

    public static final Map<Integer, List<Long>> PET_GENERATOR_MAP = ImmutableMap.<Integer, List<Long>>builder()
            .put(2, ImmutableList.of(300001L, 300002L, 300003L, 300004L))
            .put(3, ImmutableList.of(300005L, 300006L, 300007L, 300008L))
            .put(4, ImmutableList.of(300009L, 300010L, 300011L, 300012L))
            .put(5, ImmutableList.of(300013L, 300014L, 300015L, 300016L))
            .build();

    public static final List<Long> DEFINITION_IDS_BINGO = ImmutableList.of(300013L, 300014L, 300015L, 300016L);
    public static final long BROADCAST_ID_GACHA_BINGO = 3200012;

    public static final int INIT_YINGTING_AMOUNT = 0;

    public static final long RANKING_AWARD_CURRENCY_ID = 20119;
    public static final long RANKING_AWARD_CURRENCY_AMOUNT = 1;
    public static final long RANKING_AWARD_YINGTING_ID = 300017;

    public static final long POINT_REWARD_PET_GACHA = 10;
    public static final int PLAYER_LEVEL_PREREQUIREMENT = 1;

    public static final long BROADCAST_ID_RANK_7 = 3200027;
    public static final long BROADCAST_ID_RANK_10 = 3200028;
    public static final long BROADCAST_ID_SOUL_LEVEL_OVER_10 = 3200072;
    public static final long BROADCAST_ID_SOUL_NAME_ID_BINGO = 3200073;

    public static final int REFERENCE_LEVEL_NO_OWNER = 100;

    public static final long RECYCLE_BLUE_PET_GET_195 = 1000;
    public static final long RECYCLE_PURPLE_PET_GET_195 = 50000;

    public static final long PRICE_GACHA = KuaibiUnits.fromKuaibi(50);
    public static final long PRICE_WASH = 10000;

    public static int sortingIndex(int aptitudeHp, int aptitudeAtk, int aptitudePdef, int aptitudeMdef, int aptitudeSpd) {
        return (int) (aptitudeHp * 1.7
                + aptitudeAtk * 2.5
                + aptitudePdef * 1.9
                + aptitudeMdef * 1.9
                + aptitudeSpd * 2);
    }

    public static int referenceLevel(long accountId, PlayerRepository playerRepository) {
        if (accountId == KxyWebConstants.ACCOUNT_ID_NO_OWNER) {
            return REFERENCE_LEVEL_NO_OWNER;
        } else {
            return playerRepository.findById(accountId)
                    .map(p -> referenceLevel(p))
                    .orElse(REFERENCE_LEVEL_NO_OWNER);
        }
    }

    public static int referenceLevel(Player player) {
        return player.getSamsaraCount() > 0 ? 100 + player.getSamsaraCount() * 10 : player.getPlayerLevel();
    }

    public static int availableAbilityReserve(Pet pet, ResourceContext resourceContext) {
        PetInformations petInformations = resourceContext.getLoader(PetInformations.class).get(pet.getDefinitionId());
        if (petInformations.getColor() == 2) {
            return pet.getMaxAbilityCapacity() - pet.getAbilities().size();
        } else if (pet.getRank() < 4) {
            return pet.getMaxAbilityCapacity() - pet.getAbilities().size() - 1;
        } else {
            return pet.getMaxAbilityCapacity() - pet.getAbilities().size();
        }
    }

    public static long fc(Pet pet, PlayerRepository playerRepository, ResourceContext resourceContext) {
        return (long) pet.createFcParameterSpace(referenceLevel(pet.getAccountId(), playerRepository), resourceContext)
                .asRootParameterSpace()
                .getParameter(ParameterNameConstants.战斗力)
                .getValue();
    }

    private PetConstants() {
    }

}
