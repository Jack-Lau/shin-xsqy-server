/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
@Transactional
public class RedPacketService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;

    @Autowired
    RedPacketRepository redPacketRepository;
    @Autowired
    RedPacketOpenRepository redPacketOpenRepository;
    @Autowired
    RedPacketSelfRepository redPacketSelfRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redPacketRepository.count() == 0) {
            for (int i = 0; i < RedPacketConstants.MAX_SINGLE_TYPE_RED_PACKET_COUNT; i++) {
                RedPacketRecord smallRedPacketRecord = new RedPacketRecord();
                smallRedPacketRecord.setCreatorName("长乐坊坊主");
                smallRedPacketRecord.setType(0);
                smallRedPacketRecord.setFinish(false);
                redPacketRepository.save(smallRedPacketRecord);
                //
                RedPacketRecord bigRedPacketRecord = new RedPacketRecord();
                bigRedPacketRecord.setCreatorName("长乐坊坊主");
                bigRedPacketRecord.setType(1);
                bigRedPacketRecord.setFinish(false);
                redPacketRepository.save(bigRedPacketRecord);
            }
        }
    }

    public RedPacketOverall get(long accountId) {
        checkLevel(accountId);
        List<RedPacket> redPackets = new ArrayList<>();
        for (RedPacketRecord redPacketRecord : redPacketRepository.findNotFinish()) {
            redPackets.add(new RedPacket(redPacketRecord, redPacketOpenRepository.findByRedPacketId(redPacketRecord.getId())));
        }
        return new RedPacketOverall(redPackets, findOrCreate(accountId));
    }

    public List<RedPacket> today(long accountId) {
        checkLevel(accountId);
        List<RedPacket> redPackets = new ArrayList<>();
        for (RedPacketOpenRecord redPacketOpenRecord : redPacketOpenRepository.findByAccountId(accountId)) {
            redPackets.add(new RedPacket(redPacketRepository.findById(redPacketOpenRecord.getRedPacketId()).orElse(null),
                    redPacketOpenRepository.findByRedPacketId(redPacketOpenRecord.getRedPacketId())));
        }
        return redPackets;
    }

    public RedPacketOverall take(long accountId) {
        checkLevel(accountId);
        RedPacketSelfRecord redPacketSelfRecord = findOrCreate(accountId);
        if (redPacketSelfRecord.getNotTakeAmount() <= 0) {
            throw RedPacketException.noAward();
        }
        currencyService.increaseCurrency(accountId,
                CurrencyConstants.ID_毫仙石,
                redPacketSelfRecord.getNotTakeAmount(),
                CurrencyConstants.PURPOSE_INCREMENT_红包六六六);
        redPacketSelfRecord.setLastTakeAmount(redPacketSelfRecord.getNotTakeAmount());
        redPacketSelfRecord.setNotTakeAmount(0);
        redPacketSelfRepository.save(redPacketSelfRecord);
        return get(accountId);
    }

    public RedPacketOverall open(long accountId, long redPacketId) {
        checkLevel(accountId);
        RedPacketRecord redPacketRecord = redPacketRepository.findById(redPacketId).orElse(null);
        List<RedPacketOpenRecord> redPacketOpenRecords = redPacketOpenRepository.findByRedPacketId(redPacketId);
        if (redPacketRecord == null
                || redPacketRecord.isFinish()
                || redPacketOpenRecords.size() >= RedPacketConstants.MAX_SINGLE_RED_PACKET_OPEN) {
            throw RedPacketException.redPacketFinished();
        }
        //
        if (currencyService.getCurrencyAmount(accountId, CurrencyConstants.ID_毫仙石)
                < (redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST)) {
            throw RedPacketException.insufficientXS();
        }
        //
        for (RedPacketOpenRecord record : redPacketOpenRecords) {
            if (record.getAccountId() == accountId) {
                throw RedPacketException.duplicateOpen();
            }
        }
        //
        currencyService.decreaseCurrency(accountId,
                CurrencyConstants.ID_毫仙石,
                redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST,
                true,
                CurrencyConstants.PURPOSE_DECREMENT_红包六六六报名);
        RedPacketOpenRecord redPacketOpenRecord = new RedPacketOpenRecord();
        redPacketOpenRecord.setRedPacketId(redPacketId);
        redPacketOpenRecord.setAccountId(accountId);
        redPacketOpenRecord.setPlayerName(compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName());
        redPacketOpenRecord.setGainAmount(0);
        redPacketOpenRecord.setLuckyStar(false);
        redPacketOpenRepository.save(redPacketOpenRecord);
        //
        redPacketOpenRecords = redPacketOpenRepository.findByRedPacketId(redPacketId);
        if (redPacketOpenRecords.size() >= RedPacketConstants.MAX_SINGLE_RED_PACKET_OPEN) {
            redPacketRecord.setFinish(true);
            redPacketRepository.save(redPacketRecord);
            //
            Collections.shuffle(redPacketOpenRecords);
            //
            long xsAward = (redPacketRecord.getType() == 0
                    ? (RedPacketConstants.SMALL_RED_PACKET_TOTAL_AWARD - (redPacketOpenRecords.size() * RedPacketConstants.SMALL_RED_PACKET_LOWEST_AWARD))
                    : (RedPacketConstants.BIG_RED_PACKET_TOTAL_AWARD - (redPacketOpenRecords.size() * RedPacketConstants.BIG_RED_PACKET_LOWEST_AWARD)));
            int count = 0;
            RedPacketOpenRecord luckyStar = null;
            for (RedPacketOpenRecord record : redPacketOpenRecords) {
                long gainXS = (count < redPacketOpenRecords.size() - 1
                        ? (((long)(xsAward * (double)(RandomProvider.getRandom().nextInt(80) + 1) / 100)) / 1000 * 1000)
                        : xsAward);
                xsAward -= gainXS;
                record.setGainAmount(gainXS
                        + (redPacketRecord.getType() == 0 ? RedPacketConstants.SMALL_RED_PACKET_LOWEST_AWARD : RedPacketConstants.BIG_RED_PACKET_LOWEST_AWARD));
                if (luckyStar == null || record.getGainAmount() > luckyStar.getGainAmount()) {
                    luckyStar = record;
                }
                count++;
            }
            for (RedPacketOpenRecord record : redPacketOpenRecords) {
                RedPacketSelfRecord redPacketSelfRecord = findOrCreate(record.getAccountId());
                redPacketSelfRecord.setNotTakeAmount(redPacketSelfRecord.getNotTakeAmount() + record.getGainAmount());
                redPacketSelfRecord.setTotalGain(redPacketSelfRecord.getTotalGain() + record.getGainAmount());
                if (luckyStar != null && record.getId() == luckyStar.getId()) {
                    record.setLuckyStar(true);
                    redPacketSelfRecord.setTotalCost(redPacketSelfRecord.getTotalCost()
                            + (redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST));
                    redPacketSelfRecord.setNotLuckyStarCombo(0);
                    currencyService.increaseCurrency(redPacketSelfRecord.getAccountId(),
                            CurrencyConstants.ID_毫仙石,
                            redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST,
                            CurrencyConstants.PURPOSE_INCREMENT_红包六六六);
                    currencyService.decreaseCurrency(redPacketSelfRecord.getAccountId(),
                            CurrencyConstants.ID_毫仙石,
                            redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST,
                            true,
                            CurrencyConstants.PURPOSE_DECREMENT_红包六六六支付);
                    RedPacketRecord newRedPacket = new RedPacketRecord();
                    newRedPacket.setCreatorName(compositePlayerService.getPlayerBaseInfo(redPacketSelfRecord.getAccountId()).getPlayer().getPlayerName());
                    newRedPacket.setType(redPacketRecord.getType());
                    newRedPacket.setFinish(false);
                    redPacketRepository.save(newRedPacket);
                } else {
                    record.setLuckyStar(false);
                    redPacketSelfRecord.setNotTakeAmount(redPacketSelfRecord.getNotTakeAmount()
                            + (redPacketRecord.getType() == 0 ? RedPacketConstants.OPEN_SMALL_RED_PACKET_COST : RedPacketConstants.OPEN_BIG_RED_PACKET_COST));
                    redPacketSelfRecord.setNotLuckyStarCombo(redPacketSelfRecord.getNotLuckyStarCombo() + 1);
                    if (redPacketSelfRecord.getNotLuckyStarCombo() > 0 && redPacketSelfRecord.getNotLuckyStarCombo() % 10 == 0) {
                        chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                                RedPacketConstants.PER_TEN_NOT_LUCKY_STAR_BROADCAST,
                                ImmutableMap.of(
                                        "playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName(),
                                        "combo", redPacketSelfRecord.getNotLuckyStarCombo())
                        ));
                    }
                }
                redPacketSelfRepository.save(redPacketSelfRecord);
            }
            //
            redPacketOpenRepository.saveAll(redPacketOpenRecords);
        }
        //
        return get(accountId);
    }

    @Override
    public void dailyReset() {
        List<RedPacketRecord> deleteRedPacketRecords = new ArrayList<>();
        List<RedPacketOpenRecord> deleteRedPacketOpenRecords = new ArrayList<>();
        for (RedPacketRecord redPacketRecord : redPacketRepository.findAll()) {
            if (redPacketRecord.isFinish()) {
                deleteRedPacketRecords.add(redPacketRecord);
            }
        }
        //
        for (RedPacketRecord redPacketRecord : deleteRedPacketRecords) {
            deleteRedPacketOpenRecords.addAll(redPacketOpenRepository.findByRedPacketId(redPacketRecord.getId()));
        }
        //
        redPacketRepository.deleteAll(deleteRedPacketRecords);
        redPacketOpenRepository.deleteAll(deleteRedPacketOpenRecords);
    }

    private RedPacketSelfRecord findOrCreate(long accountId) {
        RedPacketSelfRecord redPacketSelfRecord = redPacketSelfRepository.findByAccountId(accountId);
        if (redPacketSelfRecord == null) {
            redPacketSelfRecord = new RedPacketSelfRecord();
            redPacketSelfRecord.setAccountId(accountId);
            redPacketSelfRecord.setTotalCost(0);
            redPacketSelfRecord.setTotalGain(0);
            redPacketSelfRecord.setNotTakeAmount(0);
            redPacketSelfRecord.setNotLuckyStarCombo(0);
            redPacketSelfRecord = redPacketSelfRepository.save(redPacketSelfRecord);
        }
        return redPacketSelfRecord;
    }

    private void checkLevel(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < RedPacketConstants.PLAYER_LEVEL_REQUIRE) {
            throw RedPacketException.insufficientPlayerLevel();
        }
    }

}
