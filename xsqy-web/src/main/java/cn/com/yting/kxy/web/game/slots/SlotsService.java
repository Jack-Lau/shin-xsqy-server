/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.award.model.Award;
import cn.com.yting.kxy.web.award.resource.Awards;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.friend.Friend;
import cn.com.yting.kxy.web.friend.FriendService;
import cn.com.yting.kxy.web.game.slots.resource.SlotsAward;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
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
public class SlotsService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;
    @Autowired
    AwardService awardService;
    @Autowired
    FriendService friendService;
    @Autowired
    MailService mailService;

    @Autowired
    SlotsRepository slotsRepository;
    @Autowired
    SlotsBigPrizeRepository slotsBigPrizeRepository;
    @Autowired
    SlotsLikeRepository slotsLikeRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        //
    }

    public SlotsOverall get(long accountId) {
        SlotsRecord sr = findOrCreate(accountId);
        return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), null);
    }

    public List<SlotsLike> getLike(long accountId) {
        return slotsLikeRepository.findByReceiverId(accountId);
    }

    public List<SlotsBigPrize> getFriendBigPrize(long accountId) {
        List<Friend> friends = friendService.get(accountId);
        List<Long> friendAccountIds = new ArrayList<>();
        friends.forEach((f) -> {
            friendAccountIds.add(f.getPlayerBaseInfo().getPlayer().getAccountId());
        });
        if (!friendAccountIds.isEmpty()) {
            List<SlotsBigPrize> result = slotsBigPrizeRepository.findByFriendAccountId(friendAccountIds);
            Collections.sort(result, (SlotsBigPrize a, SlotsBigPrize b) -> (int) (b.getId() - a.getId()));
            return result.subList(0, Math.min(result.size(), SlotsConstants.MAX_FRIEND_BIG_PRIZE_COUNT));
        }
        return new ArrayList<>();
    }

    public SlotsOverall lock(long accountId, int slotIndex, int lock) {
        checkAvailable();
        checkPlayerLevel(accountId);
        SlotsRecord sr = slotsRepository.findByAccountIdForWrite(accountId).orElse(null);
        if (sr != null) {
            List<Integer> locks = sr.getLocks();
            if (slotIndex >= 0 && slotIndex < locks.size()) {
                locks.set(slotIndex, lock);
                sr.setLocks(locks);
                if (sr.getLockCount() > 3) {
                    throw SlotsException.lockMax();
                }
                sr = slotsRepository.save(sr);
            }
            return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), null);
        }
        return null;
    }

    public SlotsOverall pull(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        SlotsRecord sr = slotsRepository.findByAccountIdForWrite(accountId).orElse(null);
        if (sr != null) {
            long cost = SlotsConstants.PULL_MILLI_KC_COST.get(sr.getLockCount());
            checkMilliKCCost(accountId, cost);
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, cost, true, CurrencyConstants.PURPOSE_DECREMENT_西游摇翻天);
            //
            List<Integer> slots = sr.getSlots();
            List<Integer> locks = sr.getLocks();
            for (int i = 0; i < slots.size(); i++) {
                if (locks.get(i) == 0) {
                    slots.set(i, RandomProvider.getRandom().nextInt(5) + 1);
                }
            }
            sr.setSlots(slots);
            sr.setTakenPrize(false);
            sr = slotsRepository.save(sr);
            return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), null);
        }
        return null;
    }

    public SlotsOverall take(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        SlotsRecord sr = slotsRepository.findByAccountIdForWrite(accountId).orElse(null);
        List<AwardResult> awardResults = new ArrayList<>();
        if (sr != null) {
            if (sr.isTakenPrize()) {
                throw SlotsException.takenPrize();
            }
            SlotsAward sa = resourceContext.getLoader(SlotsAward.class).get(getSlotsAwardId(sr.getSlots()));
            awardResults.add(awardService.processAward(accountId, sa.getAward(), CurrencyConstants.PURPOSE_INCREMENT_西游摇翻天产出));
            if (sa.getTopPrize() != 0) {
                SlotsBigPrize sbp = new SlotsBigPrize();
                sbp.setAccountId(accountId);
                sbp.setAwardId(sa.getId());
                sbp.setCreateTime(new Date(timeProvider.currentTime()));
                slotsBigPrizeRepository.save(sbp);
            }
            if (sa.getInterfaceBroadcast() != 0) {
                chatService.offerInterestingMessage(
                        sa.getInterfaceBroadcast(),
                        ChatMessage.createTemplateMessage(
                                sa.getInterfaceBroadcast(),
                                ImmutableMap.of(
                                        "playerName", compositePlayerService.getPlayerBaseInfo(sr.getAccountId()).getPlayer().getPlayerName(),
                                        "groupName", sa.getAwardName()
                                )
                        )
                );
            }
            eventPublisher.publishEvent(new SlotsTakePrizeEvent(this, sr));
            //
            sr.setLocks(SlotsConstants.DEFAULT_LOCKS);
            sr.setSlots(SlotsConstants.DEFAULT_SLOTS);
            sr.setTakenPrize(true);
            sr = slotsRepository.save(sr);
            return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), awardResults);
        }
        return null;
    }

    public SlotsOverall like(long accountId, long bigPrizeId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        SlotsRecord sr = slotsRepository.findByAccountIdForWrite(accountId).orElse(null);
        List<AwardResult> awardResults = new ArrayList<>();
        if (sr != null) {
            if (sr.getLikeSend() > SlotsConstants.MAX_LIKE_SEND_COUNT) {
                throw SlotsException.likeSendMax();
            }
            List<Long> likeIds = sr.getLikeBigPrizeIds();
            if (likeIds.contains(bigPrizeId)) {
                throw SlotsException.liked();
            }
            if (likeIds.size() >= 200) {
                likeIds.clear();
            }
            SlotsBigPrize sbp = slotsBigPrizeRepository.findById(bigPrizeId).orElse(null);
            if (sbp == null) {
                throw SlotsException.bigPrizeNotExist();
            }
            if (!friendService.friend(accountId, sbp.getAccountId())) {
                throw SlotsException.notFriend();
            }
            //
            awardResults.add(awardService.processAward(accountId, SlotsConstants.LIKE_SEND_AWARD_ID, CurrencyConstants.PURPOSE_INCREMENT_西游摇翻天产出));
            likeIds.add(bigPrizeId);
            sr.setLikeSend(sr.getLikeSend() + 1);
            sr.setLikeBigPrizeIds(likeIds);
            sr = slotsRepository.save(sr);
            //
            SlotsRecord bpsr = slotsRepository.findByAccountIdForWrite(sbp.getAccountId()).orElse(null);
            if (bpsr != null) {
                if (bpsr.getLikeReceive() < SlotsConstants.MAX_LIKE_RECEIVE_COUNT) {
                    bpsr.setLikeReceive(bpsr.getLikeReceive() + 1);
                    slotsRepository.save(bpsr);
                    //
                    SlotsLike sl = new SlotsLike();
                    sl.setSenderId(accountId);
                    sl.setReceiverId(sbp.getAccountId());
                    sl.setBigPrizeId(bigPrizeId);
                    sl.setCreateTime(new Date(timeProvider.currentTime()));
                    slotsLikeRepository.saveAndFlush(sl);
                }
            }
            return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), awardResults);
        }
        return null;
    }

    public SlotsOverall takeLike(long accountId) {
        checkAvailable();
        checkPlayerLevel(accountId);
        SlotsRecord sr = slotsRepository.findByAccountIdForWrite(accountId).orElse(null);
        List<AwardResult> awardResults = new ArrayList<>();
        if (sr != null) {
            List<SlotsLike> likes = slotsLikeRepository.findByReceiverId(accountId);
            if (likes.isEmpty()) {
                throw SlotsException.emptyLike();
            }
            likes.forEach((_item) -> {
                awardResults.add(awardService.processAward(accountId, SlotsConstants.LIKE_RECEIVE_AWARD_ID, CurrencyConstants.PURPOSE_INCREMENT_西游摇翻天产出));
            });
            slotsLikeRepository.deleteInBatch(likes);
            return new SlotsOverall(sr, getSlotsAwardId(sr.getSlots()), awardResults);
        }
        return null;
    }

    @Override
    public void dailyReset() {
        if (SlotsConstants.AVAILABLE) {
            List<SlotsRecord> records = slotsRepository.findAll();
            records.stream().map((sr) -> {
                sr.setLikeSend(0);
                return sr;
            }).forEachOrdered((sr) -> {
                sr.setLikeReceive(0);
            });
            slotsRepository.saveAll(records);
            //
            Date now = new Date(timeProvider.currentTime());
            List<SlotsLike> likes = slotsLikeRepository.findAll();
            List<SlotsLike> deleteLikes = new ArrayList<>();
            likes.stream().filter((sl) -> (now.getTime() - sl.getCreateTime().getTime() >= 172800000)).forEachOrdered((sl) -> {
                deleteLikes.add(sl);
            });
            slotsLikeRepository.deleteInBatch(deleteLikes);
        }
    }

    public void end() {
        List<SlotsRecord> srList = slotsRepository.findAll();
        List<SlotsRecord> updatedList = new ArrayList<>();
        for (SlotsRecord sr : srList) {
            if (!sr.isTakenPrize()) {
                SlotsAward sa = resourceContext.getLoader(SlotsAward.class).get(getSlotsAwardId(sr.getSlots()));
                Awards awards = Awards.getFrom(resourceContext, sa.getAward());
                long playerFc = playerRepository.findById(sr.getAccountId()).map(Player::getFc).orElse(0L);
                Award award = awards.createAward(compositePlayerService.getPlayerBaseInfo(sr.getAccountId()).getPlayer().getPlayerLevel(), playerFc);
                List<CurrencyStack> cs = new ArrayList<>();
                award.getCurrencyChanceMap().keySet().forEach((currencyId) -> {
                    if (award.getCurrencyChanceMap().get(currencyId) != 0) {
                        cs.add(new CurrencyStack(currencyId, award.getCurrencyChanceMap().get(currencyId)));
                    }
                });
                MailSendingRequest.create()
                        .template(SlotsConstants.MAIL_END_ID)
                        .attachment(cs)
                        .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_西游摇翻天产出)
                        .to(sr.getAccountId())
                        .commit(mailService);
            }
            sr.setLikeBigPrizeIds(new ArrayList<>());
            sr.setLikeSend(0);
            sr.setLikeReceive(0);
            sr.setLocks(SlotsConstants.DEFAULT_LOCKS);
            sr.setSlots(SlotsConstants.DEFAULT_SLOTS);
            sr.setTakenPrize(true);
            updatedList.add(sr);
        }
        slotsRepository.saveAll(updatedList);
        slotsBigPrizeRepository.deleteAll();
        slotsLikeRepository.deleteAll();
    }

    private SlotsRecord findOrCreate(long accountId) {
        SlotsRecord sr = slotsRepository.findByAccountId(accountId);
        if (sr == null) {
            sr = new SlotsRecord();
            sr.setAccountId(accountId);
            sr.setLikeBigPrizeIds(new ArrayList<>());
            sr.setLikeSend(0);
            sr.setLikeReceive(0);
            sr.setLocks(SlotsConstants.DEFAULT_LOCKS);
            sr.setSlots(SlotsConstants.DEFAULT_SLOTS);
            sr.setTakenPrize(true);
            sr = slotsRepository.save(sr);
        }
        return sr;
    }

    private void checkAvailable() {
        if (!SlotsConstants.AVAILABLE) {
            throw SlotsException.notStartedYet();
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < SlotsConstants.PLAYER_LEVEL_REQUIRE) {
            throw SlotsException.insufficientPlayerLevel();
        }
    }

    private void checkMilliKCCost(long accountId, long amount) {
        if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_毫仙石).getAmount() < amount) {
            throw SlotsException.insufficientCurrency();
        }
    }

    private long getSlotsAwardId(List<Integer> slots) {
        long sum = 0;
        for (Integer slot : slots) {
            sum += Math.pow(5, slot);
        }
        if (sum == 12500) {
            return 7;
        } else if (sum == 20 || sum == 100 || sum == 500 || sum == 2500) {
            return 6;
        } else if (sum == 780) {
            return 5;
        } else if (sum == 60 || sum == 260 || sum == 1260 || sum == 300 || sum == 1300 || sum == 1500) {
            return 4;
        } else if (sum == 40 || sum == 140 || sum == 640 || sum == 3140
                || sum == 80 || sum == 200 || sum == 700 || sum == 3200
                || sum == 380 || sum == 400 || sum == 1000 || sum == 3500
                || sum == 1880 || sum == 1900 || sum == 2000 || sum == 5000) {
            return 3;
        } else if (sum == 160 || sum == 660 || sum == 3160 || sum == 760 || sum == 3260 || sum == 3760 || sum == 6260
                || sum == 180 || sum == 680 || sum == 3180 || sum == 800 || sum == 3300 || sum == 3800 || sum == 6300
                || sum == 280 || sum == 880 || sum == 3380 || sum == 900 || sum == 3400 || sum == 4000 || sum == 6500
                || sum == 1280 || sum == 1380 || sum == 4380 || sum == 1400 || sum == 4400 || sum == 4500 || sum == 7500) {
            return 2;
        } else if (sum != 0) {
            return 1;
        }
        return 0;
    }

}
