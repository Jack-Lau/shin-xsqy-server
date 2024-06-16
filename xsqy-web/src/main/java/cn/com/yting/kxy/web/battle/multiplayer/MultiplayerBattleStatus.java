/*
 * Created 2018-12-7 11:14:31
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleResult.TurnInfo;

/**
 *
 * @author Azige
 */
public enum MultiplayerBattleStatus {

    INIT {
        @Override
        protected void onEntryInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        }

        @Override
        protected void onExitInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        }

        @Override
        public void onAck(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onTimeout(MultiplayerBattleService service, MultiplayerBattleSession session, Syncable syncable) {
            throw new UnsupportedOperationException();
        }
    },
    BEFORE_BATTLE {
        @Override
        protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
            return session.getId();
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            if (session.isAllAgentsSynced()) {
                session.transferTo(PREPARED, service);
            }
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            session.setFailedBeforeStart(true);
            session.transferTo(CLEAN, service);
        }
    },
    PREPARED {
        @Override
        protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
            return session.getBattleDirector().getBattleResult();
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            if (session.isAllAgentsSynced()) {
                session.transferTo(BEFORE_TURN, service);
            }
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            session.lostUnsyncedAgents();
            session.transferTo(BEFORE_TURN, service);
        }
    },
    BEFORE_TURN {
        @Override
        protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
            return null;
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            @SuppressWarnings("unchecked")
            Map<String, Object> action = (Map<String, Object>) syncMessage.getExtra();
            Long skillId = Optional.ofNullable(action.get("skillId"))
                .map(Object::toString)
                .map(Long::valueOf)
                .orElse(null);
            Long targetId = Optional.ofNullable(action.get("targetId"))
                .map(Object::toString)
                .map(Long::valueOf)
                .orElse(null);
            if (skillId != null && targetId != null) {
                session.getBattleDirector().setPlayerAction(accountId, skillId, targetId);
            }
            if (session.isAllAgentsSynced()) {
                session.transferTo(IN_TURN, service);
            }
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            session.lostUnsyncedAgents();
            session.transferTo(IN_TURN, service);
        }
    },
    IN_TURN {
        @Override
        protected void onEntryInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            BattleDirector bd = session.getBattleDirector();
            bd.nextTurn();
            if (bd.isBattleEnd()) {
                session.transferTo(AFTER_BATTLE, service);
            } else {
                session.transferTo(AFTER_TURN, service);
            }
        }

        @Override
        protected void onExitInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            throw new UnsupportedOperationException();
        }
    },
    AFTER_TURN {
        @Override
        protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
            BattleDirector bd = session.getBattleDirector();
            List<TurnInfo> turnInfo = bd.getBattleResult().getTurnInfo();
            return turnInfo.get(turnInfo.size() - 1);
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            if (session.isAllAgentsSynced()) {
                session.transferTo(BEFORE_TURN, service);
            }
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            session.lostUnsyncedAgents();
            session.transferTo(BEFORE_TURN, service);
        }
    },
    AFTER_BATTLE {
        @Override
        protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
            BattleDirector bd = session.getBattleDirector();
            List<TurnInfo> turnInfo = bd.getBattleResult().getTurnInfo();
            return turnInfo.get(turnInfo.size() - 1);
        }

        @Override
        protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            if (session.isAllAgentsSynced()) {
                session.transferTo(CLEAN, service);
            }
        }

        @Override
        protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            session.transferTo(CLEAN, service);
        }
    },
    CLEAN {
        @Override
        protected void onEntryInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
            service.announceBattleEnd(session);
            session.transferTo(END, service);
        }

        @Override
        public void onAck(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onTimeout(MultiplayerBattleService service, MultiplayerBattleSession session, Syncable syncable) {
            throw new UnsupportedOperationException();
        }
    },
    END {
        @Override
        protected void onEntryInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        }

        @Override
        protected void onExitInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        }

        @Override
        public void onAck(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onTimeout(MultiplayerBattleService service, MultiplayerBattleSession session, Syncable syncable) {
            throw new UnsupportedOperationException();
        }
    };

    protected void onEntry(MultiplayerBattleService service, MultiplayerBattleSession session) {
        if (!session.getSyncStatus().equals(this)) {
            throw new IllegalStateException(session.getSyncStatus().toString());
        }
        onEntryInternal(service, session);
    }

    protected void onEntryInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        service.startSyncing(session, this, MultiplayerBattleConstants.STATUS_TO_TIMEOUT_MAP.get(this), getSyncExtra(service, session));
    }

    protected Object getSyncExtra(MultiplayerBattleService service, MultiplayerBattleSession session) {
        return null;
    }

    protected void onExit(MultiplayerBattleService service, MultiplayerBattleSession session) {
        if (!session.getSyncStatus().equals(this)) {
            throw new IllegalStateException(session.getSyncStatus().toString());
        }
        onExitInternal(service, session);
    }

    protected void onExitInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
        session.getTimeoutFuture().cancel(false);
    }

    public void onAck(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
        synchronized (session) {
            if (!session.getSyncStatus().equals(this)) {
                throw new IllegalStateException(session.getSyncStatus().toString());
            }
            if (!syncMessage.isSyncedWith(session)) {
                return;
            }
            MultiplayerBattleClientAgent agent = session.getAgents().stream()
                .filter(it -> it.getAccountId() == accountId)
                .findAny().orElseThrow(IllegalArgumentException::new);
            agent.syncWith(session);
            onAckInternal(service, session, accountId, syncMessage);
        }
    }

    protected void onAckInternal(MultiplayerBattleService service, MultiplayerBattleSession session, long accountId, SyncMessage syncMessage) {
    }

    public void onTimeout(MultiplayerBattleService service, MultiplayerBattleSession session, Syncable syncable) {
        synchronized (session) {
            if (!session.getSyncStatus().equals(this)) {
                return;
            }
            if (!syncable.isSyncedWith(session)) {
                return;
            }
            onTimeoutInternal(service, session);
        }
    }

    protected void onTimeoutInternal(MultiplayerBattleService service, MultiplayerBattleSession session) {
    }
}
