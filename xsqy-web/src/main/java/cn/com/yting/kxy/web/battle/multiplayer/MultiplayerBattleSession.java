/*
 * Created 2018-12-7 11:13:49
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import java.util.List;
import java.util.concurrent.Future;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.web.repository.LongId;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
@Data
public class MultiplayerBattleSession implements LongId, Syncable {

    private static final Logger LOG = LoggerFactory.getLogger(MultiplayerBattleSession.class);

    private Long id;
    private BattleDirector battleDirector;
    private List<MultiplayerBattleClientAgent> agents;
    private MultiplayerBattleEndHandler battleEndHandler;
    private MultiplayerBattleStatus syncStatus = MultiplayerBattleStatus.INIT;
    private int syncNumber;
    private Future<?> timeoutFuture;
    private boolean failedBeforeStart;

    private boolean killed;

    public MultiplayerBattleSession(BattleDirector bd, List<MultiplayerBattleClientAgent> agents, MultiplayerBattleEndHandler battleEndHandler) {
        this.battleDirector = bd;
        this.agents = agents;
        this.battleEndHandler = battleEndHandler;
    }

    public void start(MultiplayerBattleService service) {
        if (!syncStatus.equals(MultiplayerBattleStatus.INIT)) {
            throw new IllegalStateException();
        }
        transferTo(MultiplayerBattleStatus.BEFORE_BATTLE, service);
    }

    protected void transferTo(MultiplayerBattleStatus status, MultiplayerBattleService service) {
        syncStatus.onExit(service, this);
        syncStatus = status;
        syncStatus.onEntry(service, this);
    }

    public boolean isAllAgentsSynced() {
        return agents.stream()
            .filter(it -> !it.isLost())
            .allMatch(it -> it.isSyncedWith(this));
    }

    public void lostUnsyncedAgents() {
        agents.stream()
            .filter(it -> !it.isSyncedWith(this))
            .forEach(it -> it.setLost(true));
    }
}
