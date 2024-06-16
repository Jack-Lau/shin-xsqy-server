/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.pk;

import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.battle.BattleService;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.battle.multiplayer.MultiplayerBattleService;
import cn.com.yting.kxy.web.battle.multiplayer.MultiplayerBattleSession;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.PlayerOnlineStatus;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Administrator
 */
@Service
public class PKService {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    BattleService battleService;
    @Autowired
    MultiplayerBattleService multiplayerBattleService;
    @Autowired
    WebsocketMessageService websocketMessageService;
    @Autowired
    @Lazy
    PKService self;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    final Cache<Long, Long> pkCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();

    public void send(long senderId, long receiverId) {
        if (pkCache.asMap().containsKey(senderId)) {
            throw PKException.requestExist();
        }
        if (compositePlayerService.getOnlineStatus(receiverId).getStatus() != PlayerOnlineStatus.Status.IDLE
                || pkCache.asMap().containsKey(receiverId)
                || pkCache.asMap().containsValue(receiverId)
                || senderId == receiverId) {
            throw PKException.receiverBusy();
        }
        //
        pkCache.put(senderId, receiverId);
        PKRequestSendEvent event = new PKRequestSendEvent(this, compositePlayerService.getPlayerDetail(senderId), compositePlayerService.getPlayerDetail(receiverId));
        websocketMessageService.sendToUser(event.getReceiver().getPlayer().getAccountId(), "/pk/handleSend", event);
    }

    public void receive(long senderId, long receiverId, boolean isOK) {
        if (!pkCache.asMap().containsKey(senderId) || pkCache.asMap().get(senderId) != receiverId) {
            throw PKException.requestNotExist();
        }
        if (compositePlayerService.getOnlineStatus(senderId).getStatus() != PlayerOnlineStatus.Status.IDLE
                || senderId == receiverId) {
            throw PKException.senderBusy();
        }
        //
        if (isOK) {
            MultiplayerBattleSession multiSession = startMultiplayerBattle(senderId, receiverId);
            PKRequestReceiveEvent event = new PKRequestReceiveEvent(this, compositePlayerService.getPlayerDetail(senderId),
                    compositePlayerService.getPlayerDetail(receiverId), isOK, null, new BattleResponse(multiSession.getId(), multiSession.getBattleDirector().getBattleResult()));
            websocketMessageService.sendToUser(event.getSender().getPlayer().getAccountId(), "/pk/handleReceive", event);
            websocketMessageService.sendToUser(event.getReceiver().getPlayer().getAccountId(), "/pk/handleReceive", event);
        } else {
            PKRequestReceiveEvent event = new PKRequestReceiveEvent(this, compositePlayerService.getPlayerDetail(senderId),
                    compositePlayerService.getPlayerDetail(receiverId), isOK, null, null);
            websocketMessageService.sendToUser(event.getSender().getPlayer().getAccountId(), "/pk/handleReceive", event);
            websocketMessageService.sendToUser(event.getReceiver().getPlayer().getAccountId(), "/pk/handleReceive", event);
        }
    }

    public void async(long senderId, long receiverId) {
        if (senderId == receiverId) {
            throw PKException.requestNotExist();
        }
        BattleSession session = startSinglePlayerBattle(senderId, receiverId);
        PKRequestReceiveEvent event = new PKRequestReceiveEvent(this, compositePlayerService.getPlayerDetail(senderId),
                compositePlayerService.getPlayerDetail(receiverId), true, new BattleResponse(session.getId(), session.getBattleDirector().getBattleResult()), null);
        websocketMessageService.sendToUser(event.getSender().getPlayer().getAccountId(), "/pk/handleReceive", event);
    }

    public void onMultiplayerBattleEnd(MultiplayerBattleSession session) {

    }

    private BattleSession startSinglePlayerBattle(long senderId, long receiverId) {
        return battleService.startAsyncPVP(senderId, Collections.singletonList(receiverId), false, false, new ArrayList<>());
    }

    private MultiplayerBattleSession startMultiplayerBattle(long senderId, long receiverId) {
        return multiplayerBattleService.startBattle(Collections.singletonList(senderId),
                Collections.singletonList(receiverId),
                true,
                false,
                self::onMultiplayerBattleEnd
        );
    }

}
