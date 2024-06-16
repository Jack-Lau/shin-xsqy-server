/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.friend.Friend;
import cn.com.yting.kxy.web.friend.FriendService;
import cn.com.yting.kxy.web.game.mineExploration.resource.MineExplorationAddLife;
import cn.com.yting.kxy.web.game.mineExploration.resource.MineExplorationAward;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
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
public class MineExplorationService implements InitializingBean, ResetTask {

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
    MineExplorationRecordRepository mineExplorationRecordRepository;
    @Autowired
    MineExplorationCouponSendRepository mineExplorationCouponSendRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    RandomSelector<Long> bigPrize_jackpot;
    RandomSelector<Long> smallPrize_jackpot;

    @Override
    public void afterPropertiesSet() throws Exception {
        RandomSelectorBuilder bigPrize_jackpot_builder = RandomSelector.<Long>builder();
        RandomSelectorBuilder smallPrize_jackpot_builder = RandomSelector.<Long>builder();
        List<MineExplorationAward> awards = new ArrayList<>(resourceContext.getLoader(MineExplorationAward.class).getAll().values());
        for (MineExplorationAward award : awards) {
            switch (award.getType()) {
                case 1:
                    bigPrize_jackpot_builder.add(award.getId(), award.getWeight());
                    break;
                default:
                    smallPrize_jackpot_builder.add(award.getId(), award.getWeight());
                    break;
            }
        }
        bigPrize_jackpot = bigPrize_jackpot_builder.build(RandomSelectType.DEPENDENT);
        smallPrize_jackpot = smallPrize_jackpot_builder.build(RandomSelectType.DEPENDENT);
    }

    public MineExplorationOverall get(long accountId) {
        MineExplorationRecord record = findOrCreate(accountId);
        MineExplorationGrid[][] map = MineExplorationRecord.toMap(record.getMap());
        MineExplorationGrid bigAwardA = null, bigAwardB = null;
        String[][] mask = MineExplorationRecord.toMask(record.getMask());
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] != null && map[i][j].type == 1) {
                    bigAwardA = map[i][j];
                }
                if (map[i][j] != null && map[i][j].type == 2) {
                    bigAwardB = map[i][j];
                }
                if (!"O".equals(mask[i][j])) {
                    if (record.isInGame()) {
                        map[i][j] = new MineExplorationGrid();
                    }
                    map[i][j].isOpen = false;
                } else {
                    map[i][j].isOpen = true;
                }
            }
        }
        return new MineExplorationOverall(record.isInGame(), record.getAvailableDig(), map, bigAwardA, bigAwardB, mineExplorationCouponSendRepository.findByReceiverId(accountId));
    }

    public MineExplorationOverall start(long accountId) {
        MineExplorationRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (record.isInGame()) {
            throw MineExplorationException.正在游戏中();
        }
        MineExplorationAddLife al = resourceContext.getLoader(MineExplorationAddLife.class).get(0);
        long kcCost = al.getPrice();
        long couponCost = Math.min(currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_代金券), kcCost / 10);
        kcCost -= couponCost * 10;
        //
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_代金券) < couponCost
                || currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_毫仙石) < kcCost * 1000) {
            throw MineExplorationException.仙石不足();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_代金券, couponCost, true, CurrencyConstants.PURPOSE_DECREMENT_矿山探宝续命);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, kcCost * 1000, true, CurrencyConstants.PURPOSE_DECREMENT_矿山探宝续命);
        //
        record.setInGame(true);
        record.setMap(createMap());
        record.setMask(MineExplorationConstants.DEFAULT_MASK);
        record.setAvailableDig(al.getAmount());
        mineExplorationRecordRepository.save(record);
        eventPublisher.publishEvent(new MineExplorationStartEvent(this, record));
        return get(accountId);
    }

    public MineExplorationOverall dig(long accountId, int row, int column) {
        MineExplorationRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (!record.isInGame()) {
            throw MineExplorationException.不在游戏();
        }
        if (record.getAvailableDig() < 1) {
            throw MineExplorationException.可挖掘次数不足();
        }
        String[][] mask = MineExplorationRecord.toMask(record.getMask());
        if (row >= mask.length
                || column >= mask[row].length
                || !"X".equals(mask[row][column])) {
            throw MineExplorationException.该格已被挖掘();
        }
        //
        mask[row][column] = "O";
        record.setAvailableDig(record.getAvailableDig() - 1);
        record.setMask(MineExplorationRecord.fromMask(mask));
        mineExplorationRecordRepository.save(record);
        return get(accountId);
    }

    public MineExplorationOverall add(long accountId) {
        MineExplorationRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (!record.isInGame()) {
            throw MineExplorationException.不在游戏();
        }
        if (record.getAvailableDig() > 0) {
            throw MineExplorationException.剩余可挖掘次数();
        }
        int currDigCount = record.getCurrDigCount();
        if (currDigCount >= MineExplorationConstants.MAX_DIG_COUNT) {
            throw MineExplorationException.没有可挖掘地块();
        }
        MineExplorationAddLife al = resourceContext.getLoader(MineExplorationAddLife.class).get(currDigCount);
        long kcCost = al.getPrice();
        //
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_毫仙石) < kcCost * 1000) {
            throw MineExplorationException.仙石不足();
        }
        //
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, kcCost * 1000, true, CurrencyConstants.PURPOSE_DECREMENT_矿山探宝续命);
        //
        record.setAvailableDig(record.getAvailableDig() + al.getAmount());
        mineExplorationRecordRepository.save(record);
        return get(accountId);
    }

    public MineExplorationOverall award(long accountId) {
        MineExplorationRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        if (!record.isInGame()) {
            throw MineExplorationException.不在游戏();
        }
        if (record.getAvailableDig() > 0) {
            throw MineExplorationException.剩余可挖掘次数();
        }
        //
        record.setInGame(false);
        mineExplorationRecordRepository.save(record);
        List<MineExplorationCouponSend> couponSends = new ArrayList<>();
        String[][] mask = MineExplorationRecord.toMask(record.getMask());
        MineExplorationGrid[][] map = MineExplorationRecord.toMap(record.getMap());
        MineExplorationGrid bigAwardAGrid = null, bigAwardBGrid = null;
        boolean sendCoupon = false;
        int bigAwardACount = 0, bigAwardBCount = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ("O".equals(mask[i][j])) {
                    MineExplorationGrid grid = map[i][j];
                    switch (grid.type) {
                        case 0:
                            break;
                        case 1:
                            bigAwardAGrid = grid;
                            bigAwardACount++;
                            break;
                        case 2:
                            bigAwardBGrid = grid;
                            bigAwardBCount++;
                            break;
                        case 3:
                            currencyService.increaseCurrency(accountId, grid.currencyId, grid.amount, CurrencyConstants.PURPOSE_INCREMENT_矿山探宝奖励);
                            break;
                        case 4:
                            sendCoupon = true;
                            List<Friend> friends = friendService.get(accountId);
                            List<Long> friendAccountIds = new ArrayList<>();
                            friends.forEach((f) -> {
                                long friendId = f.getPlayerBaseInfo().getPlayer().getAccountId();
                                friendAccountIds.add(friendId);
                                findOrCreate(friendId);
                            });
                            List<MineExplorationRecord> records = Collections.emptyList();
                            if (!friendAccountIds.isEmpty()) {
                                records = mineExplorationRecordRepository.findCanSendCoupon(friendAccountIds);
                            }
                            if (records != null && !records.isEmpty()) {
                                MineExplorationRecord r = records.get(RandomProvider.getRandom().nextInt(records.size()));
                                r.setCouponTake(r.getCouponTake() + 1);
                                mineExplorationRecordRepository.save(r);
                                //
                                MineExplorationCouponSend couponSend = new MineExplorationCouponSend();
                                couponSend.setSenderId(accountId);
                                couponSend.setReceiverId(r.getAccountId());
                                couponSend.setCreateTime(new Date(timeProvider.currentTime()));
                                couponSend.setTaken(false);
                                couponSend = mineExplorationCouponSendRepository.save(couponSend);
                                couponSends.add(couponSend);
                            }
                            break;
                        case 5:
                            break;
                    }
                } else {
                    map[i][j].isOpen = false;
                }
            }
        }
        if (bigAwardAGrid != null && bigAwardACount >= 4) {
            currencyService.increaseCurrency(accountId, bigAwardAGrid.currencyId, bigAwardAGrid.amount, CurrencyConstants.PURPOSE_INCREMENT_矿山探宝奖励);
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            3200048,
                            ImmutableMap.of(
                                    "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                    "amount", bigAwardAGrid.currencyId == CurrencyConstants.ID_毫仙石 ? KuaibiUnits.toKuaibi(bigAwardAGrid.amount) : bigAwardAGrid.amount,
                                    "currency", Currency.getFrom(resourceContext, bigAwardAGrid.currencyId).getName()
                            )
                    )
            );
            chatService.offerInterestingMessage(
                    3290004,
                    ChatMessage.createTemplateMessage(
                            3290004,
                            ImmutableMap.of(
                                    "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                    "amount", bigAwardAGrid.currencyId == CurrencyConstants.ID_毫仙石 ? KuaibiUnits.toKuaibi(bigAwardAGrid.amount) : bigAwardAGrid.amount,
                                    "currency", Currency.getFrom(resourceContext, bigAwardAGrid.currencyId).getName()
                            )
                    )
            );
        }
        if (bigAwardBGrid != null && bigAwardBCount >= 4) {
            currencyService.increaseCurrency(accountId, bigAwardBGrid.currencyId, bigAwardBGrid.amount, CurrencyConstants.PURPOSE_INCREMENT_矿山探宝奖励);
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            3200048,
                            ImmutableMap.of(
                                    "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                    "amount", bigAwardBGrid.currencyId == CurrencyConstants.ID_毫仙石 ? KuaibiUnits.toKuaibi(bigAwardBGrid.amount) : bigAwardBGrid.amount,
                                    "currency", Currency.getFrom(resourceContext, bigAwardBGrid.currencyId).getName()
                            )
                    )
            );
            chatService.offerInterestingMessage(
                    3290004,
                    ChatMessage.createTemplateMessage(
                            3290004,
                            ImmutableMap.of(
                                    "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                    "amount", bigAwardBGrid.currencyId == CurrencyConstants.ID_毫仙石 ? KuaibiUnits.toKuaibi(bigAwardBGrid.amount) : bigAwardBGrid.amount,
                                    "currency", Currency.getFrom(resourceContext, bigAwardBGrid.currencyId).getName()
                            )
                    )
            );
        }
        if (sendCoupon && couponSends.isEmpty()) {
            couponSends.add(new MineExplorationCouponSend());
        }
        return new MineExplorationOverall(record.isInGame(), record.getAvailableDig(), map, bigAwardAGrid, bigAwardBGrid, couponSends);
    }

    public MineExplorationOverall coupon(long accountId, long couponSendId) {
        MineExplorationRecord record = findOrCreate(accountId);
        checkAvailable();
        checkPlayerLevel(accountId);
        //
        MineExplorationCouponSend couponSend = mineExplorationCouponSendRepository.findById(couponSendId).orElse(null);
        if (couponSend == null) {
            throw MineExplorationException.代金券不存在();
        }
        if (couponSend.isTaken()) {
            throw MineExplorationException.代金券已领取();
        }
        couponSend.setTaken(true);
        mineExplorationCouponSendRepository.save(couponSend);
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_代金券, 10, CurrencyConstants.PURPOSE_INCREMENT_矿山探宝奖励);
        //
        return get(accountId);
    }

    public void end() {
        List<MineExplorationRecord> records = mineExplorationRecordRepository.findAll();
        List<MineExplorationRecord> updateRecords = new ArrayList<>();
        for (MineExplorationRecord record : records) {
            if (record.isInGame()) {
                List<CurrencyStack> cs = new ArrayList<>();
                String[][] mask = MineExplorationRecord.toMask(record.getMask());
                MineExplorationGrid[][] map = MineExplorationRecord.toMap(record.getMap());
                MineExplorationGrid bigAwardAGrid = null, bigAwardBGrid = null;
                int bigAwardACount = 0, bigAwardBCount = 0;
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[i].length; j++) {
                        if ("O".equals(mask[i][j])) {
                            MineExplorationGrid grid = map[i][j];
                            switch (grid.type) {
                                case 0:
                                    break;
                                case 1:
                                    bigAwardAGrid = grid;
                                    bigAwardACount++;
                                    break;
                                case 2:
                                    bigAwardBGrid = grid;
                                    bigAwardBCount++;
                                    break;
                                case 3:
                                    cs.add(new CurrencyStack(grid.currencyId, grid.amount));
                                    break;
                                case 4:
                                    break;
                                case 5:
                                    break;
                            }
                        }
                    }
                }
                if (bigAwardAGrid != null && bigAwardACount >= 4) {
                    cs.add(new CurrencyStack(bigAwardAGrid.currencyId, bigAwardAGrid.amount));
                }
                if (bigAwardBGrid != null && bigAwardBCount >= 4) {
                    cs.add(new CurrencyStack(bigAwardBGrid.currencyId, bigAwardBGrid.amount));
                }
                MailSendingRequest.create()
                        .template(59)
                        .attachment(cs)
                        .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_矿山探宝奖励)
                        .to(record.getAccountId())
                        .commit(mailService);
            }
            record.setInGame(false);
            record.setMap("");
            record.setMask("");
            record.setAvailableDig(0);
            record.setCouponTake(0);
            updateRecords.add(record);
        }
        mineExplorationRecordRepository.saveAll(updateRecords);
        mineExplorationCouponSendRepository.deleteAll();
    }

    @Override
    public void dailyReset() {
        List<MineExplorationRecord> records = mineExplorationRecordRepository.findAll();
        List<MineExplorationRecord> updateRecords = new ArrayList<>();
        for (MineExplorationRecord record : records) {
            record.setCouponTake(0);
            updateRecords.add(record);
        }
        mineExplorationRecordRepository.saveAll(updateRecords);
        //
        List<MineExplorationCouponSend> couponSends = mineExplorationCouponSendRepository.findAll();
        List<MineExplorationCouponSend> deleteSends = new ArrayList<>();
        for (MineExplorationCouponSend couponSend : couponSends) {
            if (couponSend.isTaken()) {
                deleteSends.add(couponSend);
            }
        }
        mineExplorationCouponSendRepository.deleteAll(deleteSends);
    }

    private MineExplorationRecord findOrCreate(long accountId) {
        MineExplorationRecord record = mineExplorationRecordRepository.findByAccountId(accountId);
        if (record == null) {
            record = new MineExplorationRecord();
            record.setAccountId(accountId);
            record.setInGame(false);
            record.setMap("");
            record.setMask("");
            record.setAvailableDig(0);
            record.setCouponTake(0);
            record = mineExplorationRecordRepository.save(record);
        }
        return record;
    }

    private void checkAvailable() {
        if (!MineExplorationConstants.AVAILABLE) {
            throw MineExplorationException.玩法未开启();
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < MineExplorationConstants.PLAYER_LEVEL_REQUIRE) {
            throw MineExplorationException.角色等级不足();
        }
    }

    private String createMap() {
        MineExplorationAward bigPrizeA = resourceContext.getLoader(MineExplorationAward.class).get(bigPrize_jackpot.getSingle());
        MineExplorationAward bigPrizeB = resourceContext.getLoader(MineExplorationAward.class).get(bigPrize_jackpot.getSingle());
        for (int i = 0; i < 10000; i++) {
            if (bigPrizeB.getId() != bigPrizeA.getId()) {
                break;
            }
            bigPrizeB = resourceContext.getLoader(MineExplorationAward.class).get(bigPrize_jackpot.getSingle());
        }
        //
        MineExplorationGrid[][] map = new MineExplorationGrid[5][5];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new MineExplorationGrid();
            }
        }
        //
        map = createBigAward(map, bigPrizeA, 0, 1);
        map = createBigAward(map, bigPrizeB, 0, 2);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getType() == -1) {
                    double rand = RandomProvider.getRandom().nextDouble();
                    if (rand < 0.1) {
                        map[i][j].type = 4;
                        map[i][j].currencyId = CurrencyConstants.ID_好友代金券;
                        map[i][j].amount = 10;
                    } else if (rand < 0.4) {
                        map[i][j].type = 5;
                    } else {
                        MineExplorationAward smallPrize = resourceContext.getLoader(MineExplorationAward.class).get(smallPrize_jackpot.getSingle());
                        map[i][j].type = 3;
                        map[i][j].currencyId = smallPrize.getCurrencyId();
                        map[i][j].amount = smallPrize.getAmount();
                    }
                }
            }
        }
        //
        return MineExplorationRecord.fromMap(map);
    }

    private MineExplorationGrid[][] createBigAward(MineExplorationGrid[][] map, MineExplorationAward bigAward, int count, int type) {
        if (count >= 4) {
            return map;
        }
        //
        if (count == 0) {
            for (int p = 0; p < 10000; p++) {
                int row = RandomProvider.getRandom().nextInt(map.length);
                int column = RandomProvider.getRandom().nextInt(map[0].length);
                if (map[row][column].getType() == -1) {
                    map[row][column].type = type;
                    map[row][column].currencyId = bigAward.getCurrencyId();
                    map[row][column].amount = bigAward.getAmount();
                    map = createBigAward(map, bigAward, count + 1, type);
                    //
                    List<MineExplorationGrid> fragments = new ArrayList<>();
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length; j++) {
                            if (map[i][j] != null && map[i][j].type == type) {
                                fragments.add(map[i][j]);
                            }
                        }
                    }
                    if (fragments.size() == 4) {
                        return map;
                    } else {
                        for (int i = 0; i < map.length; i++) {
                            for (int j = 0; j < map[i].length; j++) {
                                if (map[i][j] != null && map[i][j].type == type) {
                                    map[i][j] = new MineExplorationGrid();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<MineExplorationGrid> grids = new ArrayList<>();
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] != null && map[i][j].type == type) {
                        if (i - 1 >= 0 && map[i - 1][j].type == -1 && !grids.contains(map[i - 1][j])) {
                            grids.add(map[i - 1][j]);
                        }
                        if (i + 1 < map.length && map[i + 1][j].type == -1 && !grids.contains(map[i + 1][j])) {
                            grids.add(map[i + 1][j]);
                        }
                        if (j - 1 >= 0 && map[i][j - 1].type == -1 && !grids.contains(map[i][j - 1])) {
                            grids.add(map[i][j - 1]);
                        }
                        if (j + 1 < map[i].length && map[i][j + 1].type == -1 && !grids.contains(map[i][j + 1])) {
                            grids.add(map[i][j + 1]);
                        }
                    }
                }
            }
            if (!grids.isEmpty()) {
                MineExplorationGrid grid = grids.get(RandomProvider.getRandom().nextInt(grids.size()));
                grid.type = type;
                grid.currencyId = bigAward.getCurrencyId();
                grid.amount = bigAward.getAmount();
                return createBigAward(map, bigAward, count + 1, type);
            }
        }
        return map;
    }

}
