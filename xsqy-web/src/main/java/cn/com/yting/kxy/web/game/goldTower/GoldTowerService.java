/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.award.model.Award;
import cn.com.yting.kxy.web.award.resource.Awards;
import cn.com.yting.kxy.web.battle.BattleRepository;
import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.battle.BattleService;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerFloor;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerFloor.RoomContainer;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerQuestion;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerQuestionCollection;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerRoomCollection;
import cn.com.yting.kxy.web.game.goldTower.resource.GoldTowerRoomPrototype;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.party.SupportRelation;
import cn.com.yting.kxy.web.party.SupportRelationRepository;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import cn.com.yting.kxy.web.ranking.RankingElement;
import cn.com.yting.kxy.web.ranking.RankingInfo;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class GoldTowerService implements ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;
    @Autowired
    AwardService awardService;
    @Autowired
    MailService mailService;
    @Autowired
    BattleService battleService;

    @Autowired
    GoldTowerStatusEntityRepository goldTowerStatusEntityRepository;
    @Autowired
    GoldTowerRoomEntityRepository goldTowerRoomEntityRepository;
    @Autowired
    GoldTowerChallengeEntityRepository goldTowerChallengeEntityRepository;
    @Autowired
    GoldTowerRepository goldTowerRepository;
    @Autowired
    GoldTowerLogRepository goldTowerLogRepository;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    SupportRelationRepository supportRelationRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    Map<Long, GoldTowerRoomEntity> goldTower = new HashMap<>();
    final List<GoldTowerChallengeEntity> top100challengers = new ArrayList<>();

    public GoldTowerStatusEntity getGoldTowerStatusEntity() {
        return findLastGoldTowerStatusEntity(false);
    }

    public GoldTowerChallengeEntity getGoldTowerChallengeEntity(long accountId) {
        loadGoldTower();
        return findOrCreateGoldTowerChallengeEntity(accountId);
    }

    public GoldTowerRecord getGoldTowerRecord(long accountId) {
        return goldTowerRepository.findByAccountId(accountId);
    }

    public GoldTowerRoomEntity getGoldTowerRoomEntity(long roomId) {
        return goldTower.get(roomId);
    }

    public BattleResponse startWipeOutBattle(long accountId) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        //
        GoldTowerChallengeEntity gtce = ogtce.get();
        if (gtce.getLastFloorCount() != 0) {
            throw GoldTowerException.notAtFloorZero();
        }
        //
        if (gtce.getAvailableChallengeCount() < 1) {
            throw GoldTowerException.insufficientChallengeCount();
        }
        //
        GoldTowerRecord gtr = goldTowerRepository.findByAccountId(accountId);
        if (gtr == null || gtr.getMaxFinishFloor() < GoldTowerConstants.MAX_FINISH_FLOOR_REQUIRE) {
            throw GoldTowerException.insufficientMaxFinishFloor();
        }
        //
        BattleSession battleSession = battleService.startAsyncPVE(
                accountId,
                GoldTowerConstants.WIPE_OUT_BATTLE_ID,
                false,
                false,
                5,
                Collections.emptyList()
        );
        gtr.setWipeOutBattleSessionId(battleSession.getId());
        gtr.setWipeOutBattleWin(false);
        gtr.setUpToTargetFloor(false);
        gtr.setTakenWipeOutAward(false);
        goldTowerRepository.save(gtr);
        return new BattleResponse(battleSession.getId(), battleSession.getBattleDirector().getBattleResult());
    }

    public boolean tryFinishWipeOutBattle(long accountId) {
        boolean result = false;
        GoldTowerRecord gtr = goldTowerRepository.findByAccountId(accountId);
        if (gtr == null || gtr.getWipeOutBattleSessionId() == null) {
            throw GoldTowerException.insufficientMaxFinishFloor();
        }
        BattleSession battleSession = battleRepository.findById(gtr.getWipeOutBattleSessionId())
                .orElse(null);
        if (battleSession != null) {
            BattleDirector bd = battleSession.getBattleDirector();
            if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                result = true;
            } else {
                Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
                if (!ogtce.isPresent()) {
                    throw GoldTowerException.notInChallenge();
                }
                //
                GoldTowerChallengeEntity gtce = ogtce.get();
                gtce.setAvailableChallengeCount(gtce.getAvailableChallengeCount() - 1);
                if (gtce.getAvailableChallengeCount() < 1) {
                    gtce.setInChallenge(false);
                }
                goldTowerChallengeEntityRepository.save(gtce);
            }
        }
        gtr.setWipeOutBattleSessionId(null);
        gtr.setWipeOutBattleWin(result);
        goldTowerRepository.save(gtr);
        return result;
    }

    public GoldTowerWipeOut upToTargetFloor(long accountId) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        //
        GoldTowerChallengeEntity gtce = ogtce.get();
        //
        GoldTowerRecord gtr = goldTowerRepository.findByAccountId(accountId);
        if (gtr == null) {
            throw GoldTowerException.insufficientMaxFinishFloor();
        }
        //
        if (gtr.getWipeOutBattleWin() == null
                || gtr.getUpToTargetFloor() == null
                || !gtr.getWipeOutBattleWin()
                || gtr.getUpToTargetFloor()) {
            throw GoldTowerException.cannotFastUp();
        }
        //
        gtr.setUpToTargetFloor(true);
        goldTowerRepository.save(gtr);
        //
        gtce.setInChallenge(true);
        if (gtce.getAvailableChallengeCount() == GoldTowerConstants.MAX_CHALLENGE_COUNT) {
            GoldTowerStatusEntity gtse = findLastGoldTowerStatusEntity(true);
            gtse.setChallengePlayerCount(gtse.getChallengePlayerCount() + 1);
            goldTowerStatusEntityRepository.save(gtse);
        }
        gtce.setLastFloorCount(GoldTowerConstants.MAX_FINISH_FLOOR_REQUIRE);
        long targetFloorCount = gtce.getLastFloorCount() + 1;
        List<GoldTowerRoomEntity> rooms = new ArrayList<>();
        goldTower.values().stream().filter((gtre) -> (gtre.getFloorId() == targetFloorCount)).forEachOrdered((gtre) -> {
            rooms.add(gtre);
        });
        GoldTowerRoomEntity targetRoom = rooms.get(RandomProvider.getRandom().nextInt(rooms.size()));
        gtce.setCurrentRoomId(targetRoom.getId());
        gtce.setCurrentBattleSessionId(0);
        gtce.setCurrentRoomChallengeSuccess(false);
        gtce.setAvailableTreasureCount(targetRoom.getTreasureCount());
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        //
        int playerLevel = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel();
        long playerFc = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc();
        List<CurrencyStack> wipeOutAwards = new ArrayList<>();
        for (int i = 1; i < targetFloorCount; i++) {
            GoldTowerFloor floor = resourceContext.getLoader(GoldTowerFloor.class).get(i);
            if (floor != null) {
                Awards awards = Awards.getFrom(resourceContext, floor.getTreasureAwardId());
                Award award = awards.createAward(playerLevel, playerFc);
                for (int j = 0; j < floor.getTreasureLowerLimit(); j++) {
                    if (award.getExp() != 0) {
                        wipeOutAwards.add(new CurrencyStack(CurrencyConstants.ID_经验, award.getExp()));
                    }
                    award.getCurrencyChanceMap().keySet().forEach((currencyId) -> {
                        wipeOutAwards.add(new CurrencyStack(currencyId, award.getCurrencyChanceMap().get(currencyId)));
                    });
                }
            }
        }
        return new GoldTowerWipeOut(gtce, wipeOutAwards);
    }

    public GoldTowerChallengeEntity takeWipeOutAward(long accountId) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        //
        GoldTowerChallengeEntity gtce = ogtce.get();
        //
        GoldTowerRecord gtr = goldTowerRepository.findByAccountId(accountId);
        if (gtr == null) {
            throw GoldTowerException.insufficientMaxFinishFloor();
        }
        //
        if (gtr.getWipeOutBattleWin() == null
                || gtr.getUpToTargetFloor() == null
                || gtr.getTakenWipeOutAward() == null
                || !gtr.getWipeOutBattleWin()
                || !gtr.getUpToTargetFloor()
                || gtr.getTakenWipeOutAward()) {
            throw GoldTowerException.cannotTakeWipeOutAward();
        }
        //
        gtr.setWipeOutBattleWin(false);
        gtr.setTakenWipeOutAward(true);
        goldTowerRepository.save(gtr);
        //
        GoldTowerStatusEntity gtse = findLastGoldTowerStatusEntity(true);
        List<Long> awardIds = new ArrayList<>();
        for (int i = 1; i < GoldTowerConstants.MAX_FINISH_FLOOR_REQUIRE + 1; i++) {
            GoldTowerFloor floor = resourceContext.getLoader(GoldTowerFloor.class).get(i);
            if (floor != null) {
                // 发称号
                if (RandomProvider.getRandom().nextDouble() < GoldTowerConstants.CHALLENGE_SUCCESS_GET_TITLE_PROBABILITY) {
                    currencyService.increaseCurrency(accountId, GoldTowerConstants.TITLE_CURRENCY_ID, 1);
                }
                // 发宝箱
                for (int j = 0; j < floor.getTreasureLowerLimit(); j++) {
                    awardIds.add(floor.getTreasureAwardId());
                }
            }
        }
        // 发event通知活动service
        eventPublisher.publishEvent(new GoldTowerChallengeSuccessEvent(this, gtce, (int) GoldTowerConstants.MAX_FINISH_FLOOR_REQUIRE));
        //
        awardService.processAward(accountId, awardIds, CurrencyConstants.PURPOSE_INCREMENT_金光塔奖金);
        //
        goldTowerStatusEntityRepository.save(gtse);
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        return gtce;
    }

    public GoldTowerChallengeEntity startOrReturnGoldTowerChallenge(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < GoldTowerConstants.PLAYER_LEVEL_REQUIRE) {
            throw GoldTowerException.insufficientPlayerLevel();
        }
        //
        GoldTowerChallengeEntity gtce = findOrCreateGoldTowerChallengeEntity(accountId);
        if (gtce.getAvailableChallengeCount() < 1) {
            throw GoldTowerException.insufficientChallengeCount();
        }
        //
        if (gtce.isInChallenge()) {
            return gtce;
        }
        //
        gtce.setInChallenge(true);
        if (gtce.getAvailableChallengeCount() == GoldTowerConstants.MAX_CHALLENGE_COUNT) {
            GoldTowerStatusEntity gtse = findLastGoldTowerStatusEntity(true);
            gtse.setChallengePlayerCount(gtse.getChallengePlayerCount() + 1);
            goldTowerStatusEntityRepository.save(gtse);
        }
        long targetFloorCount = gtce.getLastFloorCount() + 1;
        List<GoldTowerRoomEntity> rooms = new ArrayList<>();
        goldTower.values().stream().filter((gtre) -> (gtre.getFloorId() == targetFloorCount)).forEachOrdered((gtre) -> {
            rooms.add(gtre);
        });
        GoldTowerRoomEntity targetRoom = rooms.get(RandomProvider.getRandom().nextInt(rooms.size()));
        gtce.setCurrentRoomId(targetRoom.getId());
        gtce.setCurrentBattleSessionId(0);
        gtce.setCurrentRoomChallengeSuccess(false);
        gtce.setAvailableTreasureCount(targetRoom.getTreasureCount());
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        //
        return gtce;
    }

    public BattleResponse startGoldTowerBattle(long accountId) {
        GoldTowerChallengeEntity ogtce = goldTowerChallengeEntityRepository.findByAccountId(accountId);
        if (ogtce == null) {
            throw GoldTowerException.notInChallenge();
        }
        GoldTowerChallengeEntity gtce = ogtce;
        if (!gtce.isInChallenge()) {
            throw GoldTowerException.challengeSucceed();
        }
        if (gtce.isCurrentRoomChallengeSuccess()) {
            throw GoldTowerException.challengeSucceed();
        }
        //
        BattleSession battleSession = null;
        GoldTowerRoomEntity gtre = goldTower.get(gtce.getCurrentRoomId());
        if (gtre != null) {
            GoldTowerRoomPrototype gtrp = resourceContext.getLoader(GoldTowerRoomPrototype.class).get(gtre.getPrototypeId());
            if (gtrp != null) {
                switch (gtrp.getChallengeType()) {
                    // PVP组队战
                    case 4: {
                        Player p = compositePlayerService.getPlayer(accountId);
                        List<SupportRelation> supportRelations = supportRelationRepository.findPartyMembers(accountId).stream()
                                .sorted(Comparator.comparing(it -> it.getDeadline()))
                                .limit(2)
                                .collect(Collectors.toList());
                        long selfFc = p.getFc();
                        long accountId_su1 = 0;
                        long accountId_su2 = 0;
                        double lowerDelta = Double.parseDouble(gtrp.getChallengeParam_1());
                        double upperDelta = Double.parseDouble(gtrp.getChallengeParam_2());
                        if (supportRelations.size() > 0) {
                            accountId_su1 = supportRelations.get(0).getSupporterAccountId();
                        }
                        if (supportRelations.size() > 1) {
                            accountId_su2 = supportRelations.get(1).getSupporterAccountId();
                        }
                        List<Player> enemyList = compositePlayerService.findPlayersExcludeMyTeamByFC(
                                (long) Math.floor(selfFc * lowerDelta),
                                (long) Math.floor(selfFc * upperDelta),
                                accountId,
                                accountId_su1,
                                accountId_su2,
                                3,
                                0.95,
                                1.05);
                        List<Long> enemyAccountIdList = new ArrayList<>();
                        for (Player enemy : enemyList) {
                            enemyAccountIdList.add(enemy.getAccountId());
                        }
                        battleSession = battleService.startAsyncPVP(
                                accountId,
                                enemyAccountIdList,
                                true,
                                false,
                                Collections.emptyList());
                        break;
                    }
                    // PVP单挑战
                    case 5: {
                        Player p = compositePlayerService.getPlayer(accountId);
                        long selfFc = p.getFc();
                        long accountId_su1 = 0;
                        long accountId_su2 = 0;
                        double lowerDelta = Double.parseDouble(gtrp.getChallengeParam_1());
                        double upperDelta = Double.parseDouble(gtrp.getChallengeParam_2());
                        List<Player> enemyList = compositePlayerService.findPlayersExcludeMyTeamByFC(
                                (long) Math.floor(selfFc * lowerDelta),
                                (long) Math.floor(selfFc * upperDelta),
                                accountId,
                                accountId_su1,
                                accountId_su2,
                                10,
                                0.95,
                                1.05);
                        List<Long> enemyAccountIdList = new ArrayList<>();
                        enemyAccountIdList.add(enemyList.get(RandomProvider.getRandom().nextInt(enemyList.size())).getAccountId());
                        battleSession = battleService.startAsyncPVP(
                                accountId,
                                enemyAccountIdList,
                                false,
                                false,
                                Collections.emptyList());
                        break;
                    }
                    // PVE组队战
                    case 6: {
                        battleSession = battleService.startAsyncPVE(
                                accountId,
                                Long.parseLong(gtrp.getChallengeParam_1()),
                                false,
                                false,
                                (double) gtre.getFloorId() / 10,
                                Collections.emptyList()
                        );
                        break;
                    }
                    // PVE单挑战
                    case 7: {
                        battleSession = battleService.startAsyncPVE(
                                accountId,
                                Long.parseLong(gtrp.getChallengeParam_1()),
                                false,
                                false,
                                (double) gtre.getFloorId() / 10,
                                Collections.emptyList()
                        );
                        break;
                    }
                }
            }
        }
        //
        if (battleSession == null) {
            throw GoldTowerException.challengeSucceed();
        }
        gtce.setCurrentBattleSessionId(battleSession.getId());
        goldTowerChallengeEntityRepository.save(gtce);
        return new BattleResponse(battleSession.getId(), battleSession.getBattleDirector().getBattleResult());
    }

    public GoldTowerChallengeEntity tryFinishGoldTowerChallenge(long accountId, String param) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        GoldTowerChallengeEntity gtce = ogtce.get();
        if (!gtce.isInChallenge()) {
            throw GoldTowerException.challengeSucceed();
        }
        if (gtce.isCurrentRoomChallengeSuccess()) {
            throw GoldTowerException.challengeSucceed();
        }
        if (gtce.getAvailableChallengeCount() < 1) {
            throw GoldTowerException.insufficientChallengeCount();
        }
        //
        Player p = compositePlayerService.getPlayer(accountId);
        GoldTowerRoomEntity gtre = goldTower.get(gtce.getCurrentRoomId());
        if (gtre != null) {
            GoldTowerRoomPrototype gtrp = resourceContext.getLoader(GoldTowerRoomPrototype.class).get(gtre.getPrototypeId());
            if (gtrp != null) {
                boolean isChallengeSuccess = false;
                switch (gtrp.getChallengeType()) {
                    // 寻人
                    case 1: {
                        isChallengeSuccess = true;
                        break;
                    }
                    // 答题
                    case 2: {
                        if (gtre.getChallengeParam_1() != null) {
                            long questionId = Long.parseLong(gtre.getChallengeParam_1());
                            GoldTowerQuestion gtq = resourceContext.getLoader(GoldTowerQuestion.class).get(questionId);
                            if (gtq != null) {
                                if (Integer.parseInt(param) == gtq.getAnswer()) {
                                    isChallengeSuccess = true;
                                } else {
                                    isChallengeSuccess = false;
                                }
                            }
                        }
                        break;
                    }
                    // 寻货币
                    case 3: {
                        long currencyId = Long.parseLong(gtrp.getChallengeParam_1());
                        long amount = Long.parseLong(gtrp.getChallengeParam_2());
                        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, currencyId);
                        if (currencyRecord.getAmount() < amount) {
                            throw GoldTowerException.insufficientCurrency();
                        } else {
                            currencyService.decreaseCurrency(accountId, currencyId, amount);
                            isChallengeSuccess = true;
                        }
                        break;
                    }
                    // PVP组队战
                    case 4: {
                        isChallengeSuccess = false;
                        if (gtce.getCurrentBattleSessionId() != 0) {
                            BattleSession battleSession = battleRepository.findById(gtce.getCurrentBattleSessionId())
                                    .orElse(null);
                            if (battleSession != null) {
                                BattleDirector bd = battleSession.getBattleDirector();
                                if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                                    isChallengeSuccess = true;
                                }
                            }
                        }
                        break;
                    }
                    // PVP单挑战
                    case 5: {
                        isChallengeSuccess = false;
                        if (gtce.getCurrentBattleSessionId() != 0) {
                            BattleSession battleSession = battleRepository.findById(gtce.getCurrentBattleSessionId())
                                    .orElse(null);
                            if (battleSession != null) {
                                BattleDirector bd = battleSession.getBattleDirector();
                                if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                                    isChallengeSuccess = true;
                                }
                            }
                        }
                        break;
                    }
                    // PVE组队战
                    case 6: {
                        isChallengeSuccess = false;
                        if (gtce.getCurrentBattleSessionId() != 0) {
                            BattleSession battleSession = battleRepository.findById(gtce.getCurrentBattleSessionId())
                                    .orElse(null);
                            if (battleSession != null) {
                                BattleDirector bd = battleSession.getBattleDirector();
                                if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                                    isChallengeSuccess = true;
                                }
                            }
                        }
                        break;
                    }
                    // PVE单挑战
                    case 7: {
                        isChallengeSuccess = false;
                        if (gtce.getCurrentBattleSessionId() != 0) {
                            BattleSession battleSession = battleRepository.findById(gtce.getCurrentBattleSessionId())
                                    .orElse(null);
                            if (battleSession != null) {
                                BattleDirector bd = battleSession.getBattleDirector();
                                if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                                    isChallengeSuccess = true;
                                }
                            }
                        }
                        break;
                    }
                    // 传送
                    case 8: {
                        long floorLowerLimit = Math.max(1, gtre.getFloorId() + Long.parseLong(gtrp.getChallengeParam_1()));
                        long floorUpperLimit = Math.min(gtre.getFloorId() + Long.parseLong(gtrp.getChallengeParam_2()),
                                resourceContext.getLoader(GoldTowerFloor.class).getAll().size());
                        long nextFloor = floorLowerLimit + Math.round(RandomProvider.getRandom().nextDouble() * (floorUpperLimit - floorLowerLimit));
                        List<GoldTowerRoomEntity> nextFloorRooms = new ArrayList<>();
                        for (GoldTowerRoomEntity goldTowerRoomEntity : goldTower.values()) {
                            if (goldTowerRoomEntity.getFloorId() == nextFloor) {
                                nextFloorRooms.add(goldTowerRoomEntity);
                            }
                        }
                        GoldTowerRoomEntity nextRoom = nextFloorRooms.get(RandomProvider.getRandom().nextInt(nextFloorRooms.size()));
                        gtce.setCurrentRoomId(nextRoom.getId());
                        gtce.setCurrentBattleSessionId(0);
                        gtce.setCurrentRoomChallengeSuccess(false);
                        gtce.setAvailableTreasureCount(nextRoom.getTreasureCount());
                        //
                        isChallengeSuccess = true;
                        break;
                    }
                    // 特价商人
                    case 9: {
                        if ("1".equals(param)) {
                            long currencyId = Long.parseLong(gtrp.getChallengeParam_1());
                            long amount = Long.parseLong(gtrp.getChallengeParam_2());
                            long costGoldAmount = Long.parseLong(gtrp.getChallengeParam_3());
                            if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_元宝).getAmount() < costGoldAmount) {
                                throw GoldTowerException.insufficientCurrency();
                            } else {
                                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, costGoldAmount, true);
                                currencyService.increaseCurrency(accountId, currencyId, amount);
                            }
                        }
                        isChallengeSuccess = true;
                        break;
                    }
                    // 秘密寻人
                    case 10: {
                        if (param.equals(gtrp.getChallengeParam_1())) {
                            isChallengeSuccess = true;
                        } else {
                            isChallengeSuccess = false;
                        }
                        break;
                    }
                }
                //
                if (isChallengeSuccess) {
                    gtce.setCurrentRoomChallengeSuccess(true);
                    gtce.setCurrentBattleSessionId(0);
                    gtce.setLastFloorCount(gtre.getFloorId());
                    gtce.setFinishLastFloorTime(new Date(timeProvider.currentTime()));
                    //
                    GoldTowerFloor floor = resourceContext.getLoader(GoldTowerFloor.class).get(gtre.getFloorId());
                    if (floor != null) {
                        // 挑战成功广播
                        if (floor.getBroadcastId() != null) {
                            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                                    floor.getBroadcastId(),
                                    ImmutableMap.of("playerName", p.getPlayerName())
                            ));
                        }
                    }
                    // 发event通知活动service
                    eventPublisher.publishEvent(new GoldTowerChallengeSuccessEvent(this, gtce, 1));
                    // 发称号
                    if (RandomProvider.getRandom().nextDouble() < GoldTowerConstants.CHALLENGE_SUCCESS_GET_TITLE_PROBABILITY) {
                        currencyService.increaseCurrency(accountId, GoldTowerConstants.TITLE_CURRENCY_ID, 1);
                    }
                    // 更新最高层数
                    GoldTowerRecord gtr = findOrCreateGoldTowerRecord(accountId);
                    if (gtce.getLastFloorCount() > gtr.getMaxFinishFloor()) {
                        gtr.setMaxFinishFloor(gtce.getLastFloorCount());
                        goldTowerRepository.save(gtr);
                    }
                    // 更新排行榜
                    boolean includeSelf = false;
                    GoldTowerChallengeEntity the100thChallenger = gtce;
                    int the100thChallengerIndex = -1;
                    synchronized (top100challengers) {
                        for (int i = 0; i < top100challengers.size(); i++) {
                            if (top100challengers.get(i).getAccountId() == gtce.getAccountId()) {
                                top100challengers.set(i, gtce);
                                top100challengers.sort(Comparator.reverseOrder());
                                includeSelf = true;
                            } else {
                                if (top100challengers.get(i).compareTo(the100thChallenger) < 0) {
                                    the100thChallenger = top100challengers.get(i);
                                    the100thChallengerIndex = i;
                                }
                            }
                        }
                        if (!includeSelf) {
                            if (top100challengers.size() < GoldTowerConstants.MAX_RANKING_SIZE) {
                                top100challengers.add(gtce);
                                top100challengers.sort(Comparator.reverseOrder());
                            } else {
                                if (the100thChallengerIndex != -1) {
                                    top100challengers.set(the100thChallengerIndex, gtce);
                                    top100challengers.sort(Comparator.reverseOrder());
                                }
                            }
                        }
                    }
                } else {
                    gtce.setCurrentRoomChallengeSuccess(false);
                    gtce.setCurrentBattleSessionId(0);
                    gtce.setAvailableChallengeCount(gtce.getAvailableChallengeCount() - 1);
                    gtce.setInChallenge(false);
                }
            }
        }
        //
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        return gtce;
    }

    public GoldTowerChallengeEntity openTreasure(long accountId) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        GoldTowerChallengeEntity gtce = ogtce.get();
        if (!gtce.isInChallenge()) {
            throw GoldTowerException.challengeSucceed();
        }
        //
        if (!gtce.isCurrentRoomChallengeSuccess()) {
            throw GoldTowerException.challengeSucceed();
        }
        if (gtce.getAvailableTreasureCount() < 1) {
            throw GoldTowerException.challengeSucceed();
        }
        gtce.setAvailableTreasureCount(gtce.getAvailableTreasureCount() - 1);
        GoldTowerRoomEntity gtre = goldTower.get(gtce.getCurrentRoomId());
        GoldTowerFloor floor = resourceContext.getLoader(GoldTowerFloor.class).get(gtre.getFloorId());
        if (floor != null) {
            awardService.processAward(accountId, floor.getTreasureAwardId());
        }
        //
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        return gtce;
    }

    public GoldTowerChallengeEntity gotoNextRoom(long accountId, int waypoint) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            throw GoldTowerException.notInChallenge();
        }
        GoldTowerChallengeEntity gtce = ogtce.get();
        if (!gtce.isInChallenge()) {
            throw GoldTowerException.challengeSucceed();
        }
        //
        if (!gtce.isCurrentRoomChallengeSuccess()) {
            throw GoldTowerException.challengeSucceed();
        }
        GoldTowerRoomEntity gtre = goldTower.get(gtce.getCurrentRoomId());
        GoldTowerRoomEntity nextRoom = null;
        switch (waypoint) {
            case 1: {
                if (gtre.getWaypoint_1() != null) {
                    nextRoom = goldTower.get(gtre.getWaypoint_1());
                }
                break;
            }
            case 2: {
                if (gtre.getWaypoint_2() != null) {
                    nextRoom = goldTower.get(gtre.getWaypoint_2());
                }
                break;
            }
            case 3: {
                if (gtre.getWaypoint_3() != null) {
                    nextRoom = goldTower.get(gtre.getWaypoint_3());
                }
                break;
            }
            case 4: {
                if (gtre.getWaypoint_4() != null) {
                    nextRoom = goldTower.get(gtre.getWaypoint_4());
                }
                break;
            }
        }
        if (nextRoom == null) {
            throw GoldTowerException.challengeSucceed();
        }
        gtce.setCurrentRoomId(nextRoom.getId());
        gtce.setCurrentBattleSessionId(0);
        gtce.setCurrentRoomChallengeSuccess(false);
        gtce.setAvailableTreasureCount(nextRoom.getTreasureCount());
        //
        gtce = goldTowerChallengeEntityRepository.save(gtce);
        return gtce;
    }

    public RankingInfo getGoldTowerRankingInfo(long accountId) {
        List<RankingElement> rankings = new ArrayList<>();
        RankingElement selfRanking = null;
        synchronized (top100challengers) {
            if (top100challengers.size() <= 0) {
                recoverTop100challengers();
            }
            for (int i = 0; i < top100challengers.size(); i++) {
                PlayerBaseInfo pbi = compositePlayerService.getPlayerBaseInfo(top100challengers.get(i).getAccountId());
                rankings.add(new RankingElement(pbi, i + 1, top100challengers.get(i).getLastFloorCount()));
                if (top100challengers.get(i).getAccountId() == accountId) {
                    selfRanking = new RankingElement(pbi, i + 1, top100challengers.get(i).getLastFloorCount());
                }
            }
            if (selfRanking == null) {
                PlayerBaseInfo pbi = compositePlayerService.getPlayerBaseInfo(accountId);
                GoldTowerChallengeEntity gtce = findOrCreateGoldTowerChallengeEntity(accountId);
                selfRanking = new RankingElement(pbi, 10000, gtce.getLastFloorCount());
            }
        }
        return new RankingInfo(rankings, selfRanking);
    }

    @Override
    public void dailyReset() {
        //
        goldTower.clear();
        synchronized (top100challengers) {
            top100challengers.clear();
        }
        goldTowerRoomEntityRepository.deleteAll();
        goldTowerChallengeEntityRepository.deleteAll();
        //
        GoldTowerStatusEntity gtse = new GoldTowerStatusEntity();
        gtse.setChallengePlayerCount(0);
        gtse.setStartTime(new Date(timeProvider.currentTime()));
        goldTowerStatusEntityRepository.save(gtse);
    }

    private void recoverTop100challengers() {
        List<GoldTowerChallengeEntity> challengeEntities = goldTowerChallengeEntityRepository.findAll();
        challengeEntities.sort(Comparator.reverseOrder());
        for (int i = 0; i < challengeEntities.size(); i++) {
            if (i >= GoldTowerConstants.MAX_RANKING_SIZE) {
                break;
            }
            top100challengers.add(challengeEntities.get(i));
        }
    }

    private GoldTowerStatusEntity findLastGoldTowerStatusEntity(boolean forWrite) {
        if (goldTowerStatusEntityRepository.count() == 0) {
            GoldTowerStatusEntity gtse = new GoldTowerStatusEntity();
            gtse.setChallengePlayerCount(0);
            gtse.setStartTime(new Date(timeProvider.currentTime()));
            gtse = goldTowerStatusEntityRepository.save(gtse);
            return gtse;
        }
        List<GoldTowerStatusEntity> gtseList;
        if (forWrite) {
            gtseList = goldTowerStatusEntityRepository.findAllForWrite();
        } else {
            gtseList = goldTowerStatusEntityRepository.findAll();
        }
        return gtseList.get(gtseList.size() - 1);
    }

    private GoldTowerChallengeEntity findOrCreateGoldTowerChallengeEntity(long accountId) {
        Optional<GoldTowerChallengeEntity> ogtce = goldTowerChallengeEntityRepository.findByAccountIdForWrite(accountId);
        if (!ogtce.isPresent()) {
            GoldTowerChallengeEntity gtce = new GoldTowerChallengeEntity();
            gtce.setAccountId(accountId);
            gtce.setAvailableChallengeCount(GoldTowerConstants.MAX_CHALLENGE_COUNT);
            gtce.setInChallenge(false);
            gtce.setLastFloorCount(0);
            gtce.setFinishLastFloorTime(new Date(timeProvider.currentTime()));
            //
            gtce.setCurrentRoomId(0);
            gtce.setCurrentBattleSessionId(0);
            gtce.setCurrentRoomChallengeSuccess(false);
            gtce.setAvailableTreasureCount(0);
            //
            return goldTowerChallengeEntityRepository.save(gtce);
        } else {
            return ogtce.get();
        }
    }

    private GoldTowerRecord findOrCreateGoldTowerRecord(long accountId) {
        GoldTowerRecord goldTowerRecord = goldTowerRepository.findByAccountId(accountId);
        if (goldTowerRecord == null) {
            goldTowerRecord = new GoldTowerRecord();
            goldTowerRecord.setAccountId(accountId);
            goldTowerRecord.setMaxFinishFloor(0);
            goldTowerRecord = goldTowerRepository.save(goldTowerRecord);
        }
        return goldTowerRecord;
    }

    private void loadGoldTower() {
        List<GoldTowerRoomEntity> rooms = goldTowerRoomEntityRepository.findAll();
        if (rooms == null || rooms.isEmpty()) {
            createGoldTower();
        } else if (this.goldTower.isEmpty()) {
            for (GoldTowerRoomEntity room : rooms) {
                this.goldTower.put(room.getId(), room);
            }
        }
    }

    private void createGoldTower() {
        goldTowerRoomEntityRepository.deleteAll();
        //
        Map<Long, GoldTowerRoomEntity> goldTower = new HashMap<>();
        List<GoldTowerFloor> floors = new ArrayList<>(resourceContext.getLoader(GoldTowerFloor.class).getAll().values());
        for (GoldTowerFloor gtf : floors) {
            long floorId = gtf.getId();
            for (RoomContainer rc : gtf.getRoomContainer()) {
                if (RandomProvider.getRandom().nextDouble() < rc.getProbability()) {
                    GoldTowerRoomEntity gtre = null;
                    if (rc.getSelectRoomId() >= 1000) {
                        gtre = createGoldTowerRoom(rc.getSelectRoomId(), gtf);
                    } else {
                        GoldTowerRoomCollection goldTowerRoomCollection = resourceContext.getLoader(GoldTowerRoomCollection.class).get(rc.getSelectRoomId());
                        gtre = createGoldTowerRoom(goldTowerRoomCollection.getRandomSelector().getSingle(), gtf);
                    }
                    if (gtre != null) {
                        gtre.setFloorId(floorId);
                        gtre = goldTowerRoomEntityRepository.save(gtre);
                        goldTower.put(gtre.getId(), gtre);
                    }
                }
            }
        }
        //
        for (GoldTowerRoomEntity from : goldTower.values()) {
            for (GoldTowerRoomEntity to : goldTower.values()) {
                if (from.getFloorId() + 1 == to.getFloorId()) {
                    if (RandomProvider.getRandom().nextDouble() < 0.5) {
                        GoldTowerRoomPrototype gtrp = resourceContext.getLoader(GoldTowerRoomPrototype.class).get(to.getPrototypeId());
                        if (from.getWaypoint_1() == null) {
                            from.setWaypoint_1(to.getId());
                            from.setWaypointColor_1(gtrp.getColor());
                        } else if (from.getWaypoint_2() == null) {
                            from.setWaypoint_2(to.getId());
                            from.setWaypointColor_2(gtrp.getColor());
                        } else if (from.getWaypoint_3() == null) {
                            from.setWaypoint_3(to.getId());
                            from.setWaypointColor_3(gtrp.getColor());
                        } else if (from.getWaypoint_4() == null) {
                            from.setWaypoint_4(to.getId());
                            from.setWaypointColor_4(gtrp.getColor());
                        }
                        from = goldTowerRoomEntityRepository.save(from);
                    }
                }
            }
        }
        //
        for (GoldTowerRoomEntity from : goldTower.values()) {
            if (from.getWaypoint_1() == null) {
                for (GoldTowerRoomEntity to : goldTower.values()) {
                    if (from.getFloorId() + 1 == to.getFloorId()) {
                        GoldTowerRoomPrototype gtrp = resourceContext.getLoader(GoldTowerRoomPrototype.class).get(to.getPrototypeId());
                        from.setWaypoint_1(to.getId());
                        from.setWaypointColor_1(gtrp.getColor());
                        from = goldTowerRoomEntityRepository.save(from);
                        break;
                    }
                }
            }
        }
        //
        this.goldTower = goldTower;
    }

    private GoldTowerRoomEntity createGoldTowerRoom(long prototypeId, GoldTowerFloor floor) {
        GoldTowerRoomEntity room = new GoldTowerRoomEntity();
        GoldTowerRoomPrototype prototype = resourceContext.getLoader(GoldTowerRoomPrototype.class).get(prototypeId);
        if (prototype != null) {
            room.setPrototypeId(prototypeId);
            room.setFloorId(floor.getId());
            room.setTreasureCount((int) (floor.getTreasureLowerLimit()
                    + Math.round(RandomProvider.getRandom().nextDouble() * (floor.getTreasureUpperLimit() - floor.getTreasureLowerLimit()))));
            // 生成答题的题目Id
            if (prototype.getChallengeType() == 2) {
                if (prototype.getChallengeParam_1() != null) {
                    long questionIdOrQuestionCollectionId = Long.parseLong(prototype.getChallengeParam_1());
                    if (questionIdOrQuestionCollectionId >= 1000) {
                        room.setChallengeParam_1(prototype.getChallengeParam_1());
                    } else {
                        GoldTowerQuestionCollection gtqc = resourceContext.getLoader(GoldTowerQuestionCollection.class).get(questionIdOrQuestionCollectionId);
                        if (gtqc != null) {
                            room.setChallengeParam_1(gtqc.getRandomSelector().getSingle() + "");
                        }
                    }
                }
            } else {
                if (prototype.getChallengeParam_1() != null) {
                    room.setChallengeParam_1(prototype.getChallengeParam_1());
                }
                if (prototype.getChallengeParam_2() != null) {
                    room.setChallengeParam_2(prototype.getChallengeParam_2());
                }
                if (prototype.getChallengeParam_3() != null) {
                    room.setChallengeParam_3(prototype.getChallengeParam_3());
                }
            }
        }
        return room;
    }

}
