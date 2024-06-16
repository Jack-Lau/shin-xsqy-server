/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.PrivateMessage;
import cn.com.yting.kxy.web.chat.PrivateMessageRepository;
import cn.com.yting.kxy.web.chat.model.ChatElement;
import cn.com.yting.kxy.web.chat.model.ChatElementType;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import cn.com.yting.kxy.web.player.PlayerNameUsed;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.player.PlayerNameUsedRepository;
import cn.com.yting.kxy.web.ranking.RankingService;
import cn.com.yting.kxy.web.ranking.SimpleRankingRecord;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class FriendService implements InitializingBean {

    @Autowired
    @Lazy
    CompositePlayerService compositePlayerService;
    @Autowired
    RankingService rankingService;
    @Autowired
    @Lazy
    ChatService chatService;

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    PrivateMessageRepository privateMessageRepository;
    @Autowired
    PlayerNameUsedRepository playerNameUsedRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    final Cache<Long, Friend> friendCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();
    final Cache<Long, List<Long>> applyCache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    public List<Friend> get(long accountId) {
        FriendRecord friendRecord = findOrCreate(accountId);
        List<Friend> friends = new ArrayList<>();
        friendRecord.getFriends().stream().map((id) -> {
            return getOne(accountId, id);
        }).forEachOrdered((friend) -> {
            friends.add(friend);
        });
        return friends;
    }

    public Friend getOne(long accountId) {
        Friend friend = friendCache.getIfPresent(accountId);
        if (friend == null) {
            friend = new Friend(
                    compositePlayerService.getPlayerBaseInfo(accountId),
                    compositePlayerService.isThisPlayerOnline(accountId),
                    null,
                    false);
            friendCache.put(accountId, friend);
        }
        return friend;
    }

    public Friend getOne(long actorId, long targetId) {
        Friend friend = getOne(targetId);
        PrivateMessage pm = privateMessageRepository.findLastPrivateMessage(actorId, targetId);
        if (pm != null) {
            return new Friend(
                    friend.getPlayerBaseInfo(),
                    friend.isOnline(),
                    pm.toChatMessage(),
                    pm.isAlreadyRead());
        }
        return friend;
    }

    public List<Friend> getApply(long targetId) {
        List<Friend> friends = new ArrayList<>();
        List<Long> ids = applyCache.getIfPresent(targetId);
        if (ids != null) {
            ids.stream().map((id) -> {
                return getOne(id);
            }).forEachOrdered((friend) -> {
                friends.add(friend);
            });
        }
        return friends;
    }

    public FriendRecommend recommend(long accountId) {
        List<Friend> rankingList = new ArrayList<>();
        List<Friend> fcList = new ArrayList<>();
        List<Friend> lvList = new ArrayList<>();
        //
        List<SimpleRankingRecord> srrList = rankingService.viewRanking(accountId, 4430001, FriendConstants.RECOMMEND_COUNT * 2).getTopRecords();
        for (int i = 0; i < FriendConstants.RECOMMEND_COUNT; i++) {
            SimpleRankingRecord srr = srrList.get(RandomProvider.getRandom().nextInt(srrList.size()));
            if (srr.getAccountId() != accountId) {
                Friend friend = getOne(srr.getAccountId());
                if (!rankingList.contains(friend)) {
                    rankingList.add(friend);
                }
            }
        }
        //
        PlayerBaseInfo selfInfo = compositePlayerService.getPlayerBaseInfo(accountId);
        long selfFc = selfInfo.getPlayer().getFc();
        List<Player> fcPlayerList = playerRepository.findPlayerBetweenFcExcludeSelf(accountId, selfFc - 10000, selfFc + 10000, FriendConstants.RECOMMEND_COUNT);
        fcPlayerList.stream().map((p) -> {
            return getOne(p.getAccountId());
        }).filter((friend) -> (!fcList.contains(friend))).forEachOrdered((friend) -> {
            fcList.add(friend);
        });
        //
        long selfLv = selfInfo.getPlayer().getPlayerLevel();
        List<Player> lvPlayerList = playerRepository.findPlayerBetweenLvExcludeSelf(accountId, selfLv - 10, selfLv + 10, FriendConstants.RECOMMEND_COUNT);
        lvPlayerList.stream().map((p) -> {
            return getOne(p.getAccountId());
        }).filter((friend) -> (!lvList.contains(friend))).forEachOrdered((friend) -> {
            lvList.add(friend);
        });
        //
        return new FriendRecommend(rankingList, fcList, lvList);
    }

    public Friend find(String accountIdOrName) {
        long accountId;
        if (isNumeric(accountIdOrName)) {
            accountId = Long.parseLong(accountIdOrName);
            if (!playerRepository.findById(accountId).isPresent()) {
                throw FriendException.playerNotExist();
            }
        } else {
            if (!playerRepository.existsByPlayerName(accountIdOrName)) {
                Optional<PlayerNameUsed> playerNameUsed = playerNameUsedRepository.findByUsedName(accountIdOrName);
                if (playerNameUsed.isPresent()) {
                    accountId = playerNameUsed.get().getAccountId();
                } else {
                    throw FriendException.playerNotExist();
                }
            } else {
                accountId = playerRepository.findByPlayerName(accountIdOrName).getAccountId();
            }
        }
        //
        return getOne(accountId);
    }

    public Boolean apply(long actorId, long targetId) {
        if (actorId == targetId) {
            throw FriendException.friendAlreadyExist();
        }
        FriendRecord actorRecord = findOrCreate(actorId);
        if (actorRecord.getFriends().contains(targetId)) {
            throw FriendException.friendAlreadyExist();
        }
        //
        List<Long> applyIds = applyCache.getIfPresent(targetId);
        if (applyIds != null) {
            if (applyIds.contains(actorId)) {
                throw FriendException.applyAlreadyExist();
            }
        }
        //
        if (applyIds == null) {
            applyIds = new ArrayList<>();
        }
        applyIds.add(actorId);
        synchronized (applyCache) {
            applyCache.put(targetId, applyIds);
        }
        eventPublisher.publishEvent(new FriendApplyEvent(this, targetId, getOne(actorId)));
        return true;
    }

    public Boolean handle(long actorId, long targetId, boolean pass) {
        if (actorId == targetId) {
            throw FriendException.friendAlreadyExist();
        }
        List<Long> applyIds = applyCache.getIfPresent(targetId);
        if (applyIds == null) {
            throw FriendException.applyNotExist();
        }
        if (!applyIds.contains(actorId)) {
            throw FriendException.applyNotExist();
        }
        //
        if (pass) {
            FriendRecord actorRecord = friendRepository.findByAccountIdForWrite(actorId).orElse(findOrCreate(actorId));
            FriendRecord targetRecord = friendRepository.findByAccountIdForWrite(targetId).orElse(findOrCreate(targetId));
            List<Long> actorFriends = actorRecord.getFriends();
            List<Long> targetFriends = targetRecord.getFriends();
            if (actorFriends.size() >= FriendConstants.MAX_FRIEND_COUNT) {
                throw FriendException.otherFriendCountMax();
            }
            if (targetFriends.size() >= FriendConstants.MAX_FRIEND_COUNT) {
                throw FriendException.selfFriendCountMax();
            }
            //
            if (actorFriends.contains(targetId) || targetFriends.contains(actorId)) {
                throw FriendException.friendAlreadyExist();
            }
            //
            actorFriends.add(targetId);
            targetFriends.add(actorId);
            actorRecord.setFriends(actorFriends);
            targetRecord.setFriends(targetFriends);
            friendRepository.save(actorRecord);
            friendRepository.save(targetRecord);
            //
            List<ChatElement<?>> ce = new ArrayList<>();
            ce.add(new ChatElement(ChatElementType.TEXT, FriendConstants.APPLY_PASS_TEXT));
            ChatMessage message = new ChatMessage();
            message.setBroadcast(false);
            message.setReceiverId(actorId);
            message.setElements(ce);
            chatService.sendPlayerMessage(targetId, message, false);
            //
            eventPublisher.publishEvent(new FriendApplyPassEvent(this, actorId, getOne(actorId, targetId)));
        }
        //
        applyIds.remove(actorId);
        synchronized (applyCache) {
            applyCache.put(targetId, applyIds);
        }
        //
        return pass;
    }

    public Boolean batchHandle(long targetId, boolean pass) {
        List<Long> applyIds = applyCache.getIfPresent(targetId);
        if (applyIds == null || applyIds.isEmpty()) {
            throw FriendException.applyNotExist();
        }
        //
        if (pass) {
            FriendRecord targetRecord = friendRepository.findByAccountIdForWrite(targetId).orElse(findOrCreate(targetId));
            List<Long> targetFriends = targetRecord.getFriends();
            List<ChatElement<?>> ce = new ArrayList<>();
            ce.add(new ChatElement(ChatElementType.TEXT, FriendConstants.APPLY_PASS_TEXT));
            applyIds.stream().map((actorId) -> {
                FriendRecord actorRecord = friendRepository.findByAccountIdForWrite(actorId).orElse(findOrCreate(actorId));
                List<Long> actorFriends = actorRecord.getFriends();
                if (targetFriends.size() < FriendConstants.MAX_FRIEND_COUNT
                        && actorFriends.size() < FriendConstants.MAX_FRIEND_COUNT
                        && !actorFriends.contains(targetId)
                        && !targetFriends.contains(actorId)
                        && actorId != targetId) {
                    actorFriends.add(targetId);
                    targetFriends.add(actorId);
                    actorRecord.setFriends(actorFriends);
                    ChatMessage message = new ChatMessage();
                    message.setBroadcast(false);
                    message.setReceiverId(actorId);
                    message.setElements(ce);
                    chatService.sendPlayerMessage(targetId, message, false);
                    //
                    eventPublisher.publishEvent(new FriendApplyPassEvent(this, actorId, getOne(actorId, targetId)));
                }
                return actorRecord;
            }).forEachOrdered((actorRecord) -> {
                friendRepository.save(actorRecord);
            });
            targetRecord.setFriends(targetFriends);
            friendRepository.save(targetRecord);
        }
        //
        applyIds.clear();
        synchronized (applyCache) {
            applyCache.put(targetId, applyIds);
        }
        //
        return pass;
    }

    public Boolean delete(long actorId, long targetId) {
        if (actorId == targetId) {
            throw FriendException.friendNotExist();
        }
        FriendRecord actorRecord = friendRepository.findByAccountIdForWrite(actorId).orElse(findOrCreate(actorId));
        FriendRecord targetRecord = friendRepository.findByAccountIdForWrite(targetId).orElse(findOrCreate(targetId));
        if (!actorRecord.getFriends().contains(targetId)) {
            throw FriendException.friendNotExist();
        }
        //
        List<Long> actorFriends = actorRecord.getFriends();
        List<Long> targetFriends = targetRecord.getFriends();
        actorFriends.remove(targetId);
        targetFriends.remove(actorId);
        actorRecord.setFriends(actorFriends);
        targetRecord.setFriends(targetFriends);
        friendRepository.save(actorRecord);
        friendRepository.save(targetRecord);
        return true;
    }

    public Boolean become(long actorId, long targetId) {
        boolean result = false;
        if (actorId != targetId) {
            FriendRecord actorRecord = friendRepository.findByAccountIdForWrite(actorId).orElse(findOrCreate(actorId));
            FriendRecord targetRecord = friendRepository.findByAccountIdForWrite(targetId).orElse(findOrCreate(targetId));
            List<Long> actorFriends = actorRecord.getFriends();
            List<Long> targetFriends = targetRecord.getFriends();
            List<ChatElement<?>> ce = new ArrayList<>();
            ce.add(new ChatElement(ChatElementType.TEXT, FriendConstants.APPLY_PASS_TEXT));
            if (actorFriends.size() < FriendConstants.MAX_FRIEND_COUNT
                    && targetFriends.size() < FriendConstants.MAX_FRIEND_COUNT
                    && !actorFriends.contains(targetId)
                    && !targetFriends.contains(actorId)
                    && actorId != targetId) {
                actorFriends.add(targetId);
                targetFriends.add(actorId);
                actorRecord.setFriends(actorFriends);
                targetRecord.setFriends(targetFriends);
                //
                ChatMessage message = new ChatMessage();
                message.setBroadcast(false);
                message.setReceiverId(actorId);
                message.setElements(ce);
                chatService.sendPlayerMessage(targetId, message, false);
                message.setReceiverId(targetId);
                chatService.sendPlayerMessage(actorId, message, false);
                //
                friendRepository.save(actorRecord);
                friendRepository.save(targetRecord);
                result = true;
            }
            //
            List<Long> actorApplyIds = applyCache.getIfPresent(actorId);
            List<Long> targetApplyIds = applyCache.getIfPresent(targetId);
            if (actorApplyIds != null) {
                actorApplyIds.remove(targetId);
                synchronized (applyCache) {
                    applyCache.put(actorId, actorApplyIds);
                }
            }
            if (targetApplyIds != null) {
                targetApplyIds.remove(actorId);
                synchronized (applyCache) {
                    applyCache.put(targetId, targetApplyIds);
                }
            }
        }
        //
        return result;
    }

    public Boolean friend(long actorId, long targetId) {
        boolean result = false;
        FriendRecord actorRecord = friendRepository.findByAccountId(actorId);
        if (actorRecord != null) {
            if (actorRecord.getFriends().contains(targetId)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //
    }

    private FriendRecord findOrCreate(long accountId) {
        FriendRecord friendRecord = friendRepository.findByAccountId(accountId);
        if (friendRecord == null) {
            friendRecord = new FriendRecord();
            friendRecord.setAccountId(accountId);
            friendRecord.setFriendIds(null);
            friendRecord = friendRepository.save(friendRecord);
        }
        return friendRecord;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

}
