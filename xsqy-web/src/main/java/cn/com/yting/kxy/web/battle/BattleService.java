/*
 * Created 2018-8-14 16:18:40
 */
package cn.com.yting.kxy.web.battle;

import cn.com.yting.kxy.battle.BattleConstant.FURY_MODEL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.battle.BattleDirectorBuilder.BattlePartyBuilder;
import cn.com.yting.kxy.battle.BattleResult.TurnInfo;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.battle.generate.BattleDescriptor;
import cn.com.yting.kxy.battle.generate.PartyStastics;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.player.ParameterSpaceProviderBus;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.azige.mgxy.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class BattleService implements ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(BattleService.class);

    /**
     * 战斗实例最长的生存期，单位为毫秒
     */
    private static final long BATTLE_ENTITY_LIFE_TIME = 8 * 3600 * 1000;

    /**
     * 已结束的战斗实例的最长生存期，单位为毫秒
     */
    private static final long ENDED_BATTLE_ENTITY_LIFE_TIME = 1 * 3600 * 1000;

    @Autowired
    private BattleRepository battleRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ParameterSpaceProviderBus parameterSpaceProviderBus;
    @Autowired
    private BattleUnitExporter battleUnitExporter;

    private final Cache<Long, Long> nextOperationTimeMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();
    private final long waitTimePerAction = 250;

    /**
     *
     * @param accountId
     * @param battleDescriptorId
     * @param oneshot 生成一场直接进行到结束的战斗
     * @param openSummon
     * @param str 强度参数
     * @param eventHandlers
     * @return
     */
    //读写
    public BattleSession startAsyncPVE(
            long accountId,
            long battleDescriptorId,
            boolean oneshot,
            boolean openSummon,
            double str,
            List<EventHandler> eventHandlers) {
        Player player = playerRepository.findById(accountId).get();

        BattleDirectorBuilder builder = new BattleDirectorBuilder();
        BattlePartyBuilder playerPartyBuilder = builder.redParty();
        BattleDescriptor battleDescriptor = resourceContext.getLoader(BattleDescriptor.class).get(battleDescriptorId);

        if (battleDescriptor.isSinglePlayerLimited()) {
            battleUnitExporter.exportSinglePlayerParty(playerPartyBuilder, player, Stance.STANCE_RED, true, openSummon, 1);
        } else {
            battleUnitExporter.exportPlayerPartyWithSupport(playerPartyBuilder, player, Stance.STANCE_RED, true, openSummon);
        }

        battleDescriptor.export(builder, resourceContext);

        eventHandlers.forEach(builder::handler);
        //
        builder.redPartyHpVisible(true);
        builder.bluePartyHpVisible(true);
        //
        BattleDirector bd = builder.build();
        if (oneshot) {
            bd.oneshot();
        } else {
            bd.battleStart();
        }
        BattleSession battleSession = new BattleSession();
        battleSession.setAccountId(accountId);
        battleSession.setBattleDescriptorId(battleDescriptorId);
        battleSession.setCreateTime(new Date(timeProvider.currentTime()));
        battleSession.setBattleDirector(bd);
        battleRepository.save(battleSession);

        return battleSession;
    }

    public BattleSession startAsyncPVP(
            long accountId,
            List<Long> opponentAccountIds,
            boolean hasSupport,
            boolean openSummon,
            List<EventHandler> eventHandlers) {
        return startAsyncPVP(accountId, null, opponentAccountIds, hasSupport, openSummon, eventHandlers);
    }

    public BattleSession startAsyncPVP(
            long accountId,
            List<UnitBuilder<?>> teamUnitBuilders,
            List<Long> opponentAccountIds,
            boolean openSummon,
            List<EventHandler> eventHandlers) {
        return startAsyncPVP(accountId, teamUnitBuilders, opponentAccountIds, false, openSummon, eventHandlers);
    }

    public BattleSession startAsyncPVP(
            long accountId,
            List<UnitBuilder<?>> teamUnitBuilders,
            List<Long> opponentAccountIds,
            boolean hasSupport,
            boolean openSummon,
            List<EventHandler> eventHandlers) {
        Player player = playerRepository.findById(accountId).get();
        BattleDirectorBuilder builder = new BattleDirectorBuilder();
        if (!hasSupport || teamUnitBuilders == null) {
            builder.furyModel(FURY_MODEL.ASYNC_PVP_SINGLE);
        } else {
            builder.furyModel(FURY_MODEL.ASYNC_PVP_TEAM);
        }
        builder.bluePartyHpVisible(false);

        //我方助战
        BattlePartyBuilder playerPartyBuilder = builder.redParty();
        if (hasSupport) {
            battleUnitExporter.exportPlayerPartyWithSupport(playerPartyBuilder, player, Stance.STANCE_RED, true, openSummon);
        } else {
            battleUnitExporter.exportSinglePlayerParty(playerPartyBuilder, player, Stance.STANCE_RED, true, openSummon, 1);
            if (teamUnitBuilders != null) {
                for (int i = 0; i < teamUnitBuilders.size(); i++) {
                    playerPartyBuilder.unit(i + 2, teamUnitBuilders.get(i));
                }
            }
        }

        //敌方队伍
        BattlePartyBuilder opponentPartyBuilder = builder.blueParty();
        Player mainOpponentPlayer = playerRepository.findById(opponentAccountIds.get(0)).get();
        battleUnitExporter.exportSinglePlayerParty(opponentPartyBuilder, mainOpponentPlayer, Stance.STANCE_BLUE, false, openSummon, 1);
        for (int i = 1; i < opponentAccountIds.size(); i++) {
            Player subOpponentPlayer = playerRepository.findById(opponentAccountIds.get(i)).get();
            battleUnitExporter.exportPlayer(subOpponentPlayer, opponentPartyBuilder.unit(i + 1), Stance.STANCE_BLUE, false, openSummon);
        }

        eventHandlers.forEach(builder::handler);
        //
        builder.redPartyHpVisible(true);
        builder.bluePartyHpVisible(true);
        //
        BattleDirector bd = builder.build();
        bd.battleStart();

        BattleSession battleSession = new BattleSession();
        battleSession.setAccountId(accountId);
        battleSession.setBattleDescriptorId(null);
        battleSession.setCreateTime(new Date(timeProvider.currentTime()));
        battleSession.setBattleDirector(bd);
        battleRepository.save(battleSession);

        return battleSession;
    }

    public TurnInfo nextTurn(long accountId, long battleSessionId, int turnCount, Long skillId, Long targetId) {
        BattleSession battleSession = battleRepository.findById(battleSessionId)
                .orElseThrow(() -> KxyWebException.notFound("战斗会话不存在，id=" + battleSessionId));
        Long nextTime = nextOperationTimeMap.getIfPresent(accountId);
        if (nextTime != null && nextTime > timeProvider.currentTime()) {
//            throw BattleException.operationTooOften();
        }

        synchronized (battleSession) {
            BattleDirector bd = battleSession.getBattleDirector();
            if (bd.getTurnCount() + 1 < turnCount) {
                throw KxyWebException.unknown("无效的回合数：" + turnCount + "，当前实际要进行的回合数：" + (bd.getTurnCount() + 1));
            }
            if (bd.getTurnCount() + 1 > turnCount) {
                return bd.getBattleResult().getTurnInfo().get(turnCount - 1);
            }
            if (bd.isBattleEnd()) {
                throw KxyWebException.unknown("战斗已结束");
            }

            if (skillId != null && targetId != null) {
                try {
                    bd.setPlayerAction(accountId, skillId, targetId);
                } catch (IllegalArgumentException e) {
//                    throw BattleException.operationIncorrect();
                }
            }

            bd.nextTurn();

            TurnInfo turnInfo = bd.getBattleResult().getTurnInfo().get(turnCount - 1);
            nextTime = timeProvider.currentTime() + turnInfo.getActionRecord().size() * waitTimePerAction;
            nextOperationTimeMap.put(accountId, nextTime);
            return turnInfo;
        }
    }

    public BattleResponse finish(long accountId, long battleSessionId) {
        BattleSession battleSession = battleRepository.findById(battleSessionId)
                .orElseThrow(() -> KxyWebException.notFound("战斗会话不存在，id=" + battleSessionId));

        synchronized (battleSession) {
            BattleDirector bd = battleSession.getBattleDirector();
            bd.finishBattleByAutoNextTurn();
            return new BattleResponse(battleSessionId, bd.getBattleResult());
        }
    }

    public void clean(long accountId) {
        StreamSupport.stream(battleRepository.findAll().spliterator(), false)
                .filter(it -> (!it.getBattleDirector().isBattleEnd() && it.getAccountId() == accountId))
                .forEach(it -> finish(accountId, it.getId()));
    }

    @Override
    public void hourlyReset() {
        LOG.info("开始清理过期的战斗实例");
        Date deadline = new Date(timeProvider.currentTime() - BATTLE_ENTITY_LIFE_TIME);
        Date endedDeadline = new Date(timeProvider.currentTime() - ENDED_BATTLE_ENTITY_LIFE_TIME);
        List<BattleSession> list = StreamSupport.stream(battleRepository.findAll().spliterator(), false)
                .filter(it -> (!it.getBattleDirector().isBattleEnd() && it.getCreateTime().before(deadline))
                || (it.getBattleDirector().isBattleEnd() && it.getCreateTime().before(endedDeadline)))
                //                .peek(it -> {
                //                    if (it.getBattleDirector().isBattleEnd()) {
                //                        LOG.info("清理战斗实例，已结束的战斗，id={}", it.getId());
                //                    } else {
                //                        LOG.info("清理战斗实例，未结束的战斗，id={}", it.getId());
                //                    }
                //                })
                .collect(Collectors.toList());
        battleRepository.deleteAll(list);
        LOG.info("清理了 {} 个战斗实例", list.size());
    }

}
