/*
 * Created 2018-12-7 11:09:56
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.BattleConstant.FURY_MODEL;
import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.battle.PartyBuilder;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.battle.BattleUnitExporter;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Azige
 */
@Service
public class MultiplayerBattleService implements ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(MultiplayerBattleService.class);

    @Autowired
    private MultiplayerBattleRepository multiplayerBattleRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private WebsocketMessageService websocketMessageService;

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private BattleUnitExporter battleUnitExporter;

    public MultiplayerBattleSession startBattle(
            List<Long> redPartyAccountIds,
            List<Long> bluePartyAccountIds,
            boolean singleBattle,
            boolean openSummon,
            MultiplayerBattleEndHandler battleEndHandler) {
        BattleDirectorBuilder builder = new BattleDirectorBuilder();
        builder.furyModel(singleBattle ? FURY_MODEL.SYNC_PVP_SINGLE : FURY_MODEL.SYNC_PVP_TEAM);
        //
        exportParty(redPartyAccountIds, builder.redParty(), Stance.STANCE_RED, openSummon);
        exportParty(bluePartyAccountIds, builder.blueParty(), Stance.STANCE_BLUE, openSummon);
        //
        builder.redPartyHpVisible(true);
        builder.bluePartyHpVisible(true);
        //
        BattleDirector bd = builder.build();
        bd.battleStart();

        List<MultiplayerBattleClientAgent> agents = new ArrayList<>();
        redPartyAccountIds.forEach((id) -> {
            agents.add(new MultiplayerBattleClientAgent(id));
        });
        bluePartyAccountIds.forEach((id) -> {
            agents.add(new MultiplayerBattleClientAgent(id));
        });
        MultiplayerBattleSession battleSession = new MultiplayerBattleSession(bd, agents, battleEndHandler);
        multiplayerBattleRepository.save(battleSession);

        battleSession.start(this);

        return battleSession;
    }

    private void exportParty(
            List<Long> accountIds,
            PartyBuilder<?> partyBuilder,
            Stance stance,
            boolean openSummon) {
        for (int i = 0; i < accountIds.size() && i < 3; i++) {
            Player player = playerRepository.findById(accountIds.get(i)).get();
            battleUnitExporter.exportPlayer(player, partyBuilder.unit(i + 1), stance, true, openSummon);
            if (!openSummon && i == 0) {
                battleUnitExporter.exportPets(partyBuilder, player, stance);
            }
        }
    }

    protected void startSyncing(MultiplayerBattleSession session, MultiplayerBattleStatus status, Duration timeout, Object extra) {
        if (session.isKilled()) {
            return;
        }
        int syncNumber = RandomProvider.getRandom().nextInt();
        session.setSyncNumber(syncNumber);
        SyncMessage message = new SyncMessage();
        message.setSyncStatus(status);
        message.setSyncNumber(syncNumber);
        message.setExtra(extra);
        session.getAgents().forEach(agent -> {
            websocketMessageService.sendToUser(agent.getAccountId(), "/multiplayerBattle/sync", message);
        });
        session.setTimeoutFuture(taskScheduler.schedule(() -> {
            status.onTimeout(this, session, message);
        }, timeProvider.currentInstant().plus(timeout)));
    }

    public void sync(long accountId, long sessionId, SyncMessage message) {
        MultiplayerBattleSession session = multiplayerBattleRepository.findById(sessionId).orElseThrow(() -> KxyWebException.notFound("战斗会话不存在"));
        session.getSyncStatus().onAck(this, session, accountId, message);
    }

    public List<Long> findAttendingSessionIds(long accountId) {
        return multiplayerBattleRepository.findAll().stream()
                .filter(it -> !Objects.equals(it.getSyncStatus(), MultiplayerBattleStatus.END))
                .filter(it -> it.getAgents().stream().anyMatch(a -> a.getAccountId() == accountId))
                .map(MultiplayerBattleSession::getId)
                .collect(Collectors.toList());
    }

    public void clean() {
        multiplayerBattleRepository.findAll().forEach(it -> it.setKilled(true));
        multiplayerBattleRepository.deleteAll();
    }

    protected void announceBattleEnd(MultiplayerBattleSession session) {
        MultiplayerBattleEndHandler battleEndHandler = session.getBattleEndHandler();
        if (battleEndHandler != null) {
            battleEndHandler.onBattleEnd(session);
        }
    }

    @Override
    public void hourlyReset() {
        List<MultiplayerBattleSession> list = multiplayerBattleRepository.findAll().stream()
                .filter(it -> it.getSyncStatus().equals(MultiplayerBattleStatus.END))
                .collect(Collectors.toList());
        multiplayerBattleRepository.deleteAll(list);
        LOG.info("清理了 {} 个多人战斗会话", list.size());
    }

    @Scheduled(cron = "0 30 13 * * *")
    public void dailyCleanup() {
        multiplayerBattleRepository.deleteAll();
    }
}
