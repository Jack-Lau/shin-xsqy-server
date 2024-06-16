/*
 * Created 2018-10-31 12:43:01
 */
package cn.com.yting.kxy.web.ranking;

import static java.util.stream.Collectors.*;

import java.util.Comparator;
import java.util.Map;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentConstants;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetConstants;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerLevelAndExp;
import cn.com.yting.kxy.web.player.PlayerRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class RankingUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(RankingUpdater.class);

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private ResourceContext resourceContext;

    @Scheduled(fixedDelay = 300_000)
    public void updateAll() {
        Flowable.<Runnable>fromArray(
            this::updatePlayerFcRanking,
            this::update凌霄战力榜,
            this::update普陀战力榜,
            this::update盘丝战力榜,
            this::update五庄战力榜,
            this::updatePlayerLevelRanking,
            this::updatePetRanking,
            this::updateEquipmentRanking
        )
            // 忽略任何单个排行榜的更新失败
            .flatMapCompletable(it -> Completable
                .fromRunnable(it)
                .doOnError(ex -> LOG.error("", ex))
                .onErrorComplete()
            )
            .subscribe();
    }

    public void updatePlayerFcRanking() {
        Map<Long, RankingValues> map = playerRepository.findAll().stream()
            .sorted(Comparator.comparing(Player::getFc).reversed())
            .limit(100)
            .collect(toMap(Player::getAccountId, p -> RankingValues.builder().rankingValue_1(-p.getFc()).build()));
        rankingService.updateAllRankingValueByAccountId(4430001, map);
    }

    public void update凌霄战力榜() {
        updateSchoolPlayerFcRanking(4430002, 101);
    }

    public void update普陀战力榜() {
        updateSchoolPlayerFcRanking(4430003, 102);
    }

    public void update盘丝战力榜() {
        updateSchoolPlayerFcRanking(4430004, 103);
    }

    public void update五庄战力榜() {
        updateSchoolPlayerFcRanking(4430005, 104);
    }

    private void updateSchoolPlayerFcRanking(long rankingId, long schoolId) {
        Map<Long, RankingValues> map = playerRepository.findBySchoolId(schoolId).stream()
            .sorted(Comparator.comparing(Player::getFc).reversed())
            .limit(100)
            .collect(toMap(Player::getAccountId, p -> RankingValues.builder().rankingValue_1(-p.getFc()).build()));
        rankingService.updateAllRankingValueByAccountId(rankingId, map);
    }

    public void updatePlayerLevelRanking() {
        Map<Long, RankingValues> map = playerRepository.findPlayerLevelAndExp().stream()
            .sorted(Comparator.comparing(PlayerLevelAndExp::getLevel).reversed().thenComparing(Comparator.comparing(PlayerLevelAndExp::getExp).reversed()))
            .limit(100)
            .collect(toMap(PlayerLevelAndExp::getAccountId, it -> RankingValues.builder()
                .rankingValue_1(-it.getLevel())
                .rankingValue_2(-it.getExp())
                .build()));
        rankingService.updateAllRankingValueByAccountId(4430006, map);
    }

    public void updatePetRanking() {
        Map<Long, Map<Long, RankingValues>> map = petRepository.findInBattlePets().stream()
            .collect(groupingBy(
                Pet::getAccountId,
                toMap(Pet::getId, pet -> RankingValues.builder().rankingValue_1(-PetConstants.fc(pet, playerRepository, resourceContext)).build())
            ));
        rankingService.updateAllRankingValue(4430007, map);
    }

    public void updateEquipmentRanking() {
        Map<Long, RankingValues> map = equipmentRepository.findArmedEquipments().stream()
            .collect(groupingBy(
                Equipment::getAccountId,
                mapping(
                    equipment -> EquipmentConstants.fc(equipment, resourceContext),
                    collectingAndThen(reducing(0L, Long::sum), fc -> RankingValues.builder().rankingValue_1(-fc).build())
                )
            ));
        rankingService.updateAllRankingValueByAccountId(4430008, map);
    }
}
