/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.game.idleMine.IdleMineRecord.IdleMineQueue;
import cn.com.yting.kxy.web.game.idleMine.IdleMineRecord.IdleMineReward;
import cn.com.yting.kxy.web.game.idleMine.resource.ExpeditionTeamInfo;
import cn.com.yting.kxy.web.game.idleMine.resource.LuckyInfo;
import cn.com.yting.kxy.web.game.idleMine.resource.MapProductionInfo;
import cn.com.yting.kxy.web.game.idleMine.resource.MapProductionInfo.ExpeditionTeam;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.PlayerDetail;
import cn.com.yting.kxy.web.price.PriceService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class IdleMineService {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    PriceService priceService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;

    @Autowired
    IdleMineRepository idleMineRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    public IdleMineRecord get(long accountId) {
        return findOrCreate(accountId);
    }

    public List<IdleMinePrice> price(long accountId) {
        List<IdleMinePrice> prices = new ArrayList<>();
        resourceContext.getLoader(MapProductionInfo.class).getAll().values().stream().map((info) -> {
            IdleMinePrice price = new IdleMinePrice();
            price.setMapId(info.getId());
            price.setKcPrice(priceService.getCurrentPrice(info.getKcFloatingPrice()));
            price.setGoldPrice(priceService.getCurrentPrice(info.getGoldFloatingPrice()));
            return price;
        }).forEachOrdered((price) -> {
            prices.add(price);
        });
        return prices;
    }

    public IdleMineRecord hire(long accountId, long teamId, long mapId, long activePointsToUse, long expectedPrice) {
        checkPlayerLevel(accountId);
        Optional<IdleMineRecord> orecord = idleMineRepository.findByAccountIdForWrite(accountId);
        if (orecord.isPresent()) {
            IdleMineRecord record = orecord.get();
            ExpeditionTeamInfo teamInfo = resourceContext.getLoader(ExpeditionTeamInfo.class).get(teamId);
            MapProductionInfo mapInfo = resourceContext.getLoader(MapProductionInfo.class).get(mapId);
            //
            if (compositePlayerService.getPlayerLevel(accountId) < mapInfo.getExpeditionRequireLevel()) {
                throw IdleMineException.insufficientMapPlayerLevel();
            }
            //
            ExpeditionTeam team = mapInfo.getExpeditionTeamById(teamId);
            if (teamInfo.getHireCurrencyId() == CurrencyConstants.ID_毫仙石) {
                priceService.deduct(accountId, mapInfo.getKcFloatingPrice(), expectedPrice, CurrencyConstants.PURPOSE_DECREMENT_三界经商雇佣商队, teamInfo.getFloatingWeight(), teamInfo.getPriceMultiple(), activePointsToUse);
            } else {
                priceService.deduct(accountId, mapInfo.getGoldFloatingPrice(), expectedPrice, CurrencyConstants.PURPOSE_DECREMENT_三界经商雇佣商队, teamInfo.getFloatingWeight(), teamInfo.getPriceMultiple(), activePointsToUse);
            }
            boolean hire = false;
            //
            List<IdleMineQueue> queues = record.getIdleMineQueueList();
            for (int i = 0; i < queues.size(); i++) {
                if (queues.get(i).getTeamId() == null && record.getAvailableMineQueueCount() > i) {
                    long currentTime = timeProvider.currentTime();
                    queues.set(i, new IdleMineQueue(teamId, mapId, new Date(currentTime + secondToMilliSecond(team.getTotalTime())), new Date(currentTime)));
                    record.setIdleMineQueueList(queues);
                    hire = true;
                    break;
                }
            }
            //
            if (!hire) {
                throw IdleMineException.insufficientMineQueueCount();
            }
            record = idleMineRepository.save(record);
            return record;
        }
        return null;
    }

    public IdleMineRecord balance(long accountId) {
        checkPlayerLevel(accountId);
        Optional<IdleMineRecord> orecord = idleMineRepository.findByAccountIdForWrite(accountId);
        if (orecord.isPresent()) {
            IdleMineRecord record = orecord.get();
            long currentTime = timeProvider.currentTime();
            List<IdleMineQueue> queues = record.getIdleMineQueueList();
            List<IdleMineQueue> newQueues = new ArrayList<>();
            for (IdleMineQueue queue : queues) {
                if (queue.getTeamId() != null) {
                    IdleMineQueue newQueue = new IdleMineQueue();
                    newQueue.setTeamId(queue.getTeamId());
                    newQueue.setMapId(queue.getMapId());
                    newQueue.setFinishTime(queue.getFinishTime());
                    ExpeditionTeamInfo teamInfo = resourceContext.getLoader(ExpeditionTeamInfo.class).get(queue.getTeamId());
                    MapProductionInfo mapInfo = resourceContext.getLoader(MapProductionInfo.class).get(queue.getMapId());
                    ExpeditionTeam team = mapInfo.getExpeditionTeamById(queue.getTeamId());
                    //
                    long lastBalanceTime = queue.getLastBalanceTime().toInstant().toEpochMilli();
                    long deltaTime = Math.min(currentTime, queue.getFinishTime().toInstant().toEpochMilli()) - lastBalanceTime;
                    long balanceInstant;
                    if (teamInfo.getHireCurrencyId() == CurrencyConstants.ID_毫仙石) {
                        balanceInstant = secondToMilliSecond(mapInfo.getKcUnitTime());
                    } else {
                        balanceInstant = secondToMilliSecond(mapInfo.getGoldUnitTime());
                    }
                    long balanceCount = deltaTime / balanceInstant;
                    long rewardId = record.getIdleMineRewardList().size();
                    List<IdleMineReward> rewards = new ArrayList<>();
                    for (long i = 0; i < balanceCount; i++) {
                        rewardId++;
                        rewards.add(new IdleMineReward(rewardId, mapInfo.getProduceCurrencyId(), team.getEfficiency()));
                        lastBalanceTime += balanceInstant;
                    }
                    //
                    record.addIdleMineReward(rewards);
                    newQueue.setLastBalanceTime(new Date(lastBalanceTime));
                    //
                    if (currentTime >= queue.getFinishTime().toInstant().toEpochMilli()) {
                        LuckyInfo luckyInfo = null;
                        for (LuckyInfo li : resourceContext.getLoader(LuckyInfo.class).getAll().values()) {
                            if (li.getExpeditionTeamId() == teamInfo.getId() && li.getExploreCurrencyId() == mapInfo.getProduceCurrencyId()) {
                                luckyInfo = li;
                                break;
                            }
                        }
                        if (luckyInfo != null) {
                            PlayerDetail pd = compositePlayerService.getPlayerDetail(accountId);
                            double luckyValue = 0.0;
                            for (Parameter p : pd.getParameters()) {
                                if (p.getName().equals(ParameterNameConstants.幸运)) {
                                    luckyValue = p.getValue();
                                    break;
                                }
                            }
                            double luckyProbability = Math.min(luckyValue / 2000 * luckyInfo.getProbability(), luckyInfo.getProbability());
                            if (RandomProvider.getRandom().nextDouble() < luckyProbability) {
                                rewardId++;
                                record.addIdleMineReward(Arrays.asList(new IdleMineReward(rewardId, luckyInfo.getExploreCurrencyId(), luckyInfo.getAmount())));
                                if (luckyInfo.getBroadcast() != 0) {
                                    chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                                            luckyInfo.getBroadcast(),
                                            ImmutableMap.of(
                                                    "playerName", pd.getPlayer().getPlayerName(),
                                                    "expeditionTeam", teamInfo.getName(),
                                                    "amount", luckyInfo.getAmount(),
                                                    "currency", Currency.getFrom(resourceContext, luckyInfo.getExploreCurrencyId()).getName())
                                    ));
                                }
                            }
                        }
                        //
                        newQueue = new IdleMineQueue();
                    }
                    newQueues.add(newQueue);
                } else {
                    newQueues.add(new IdleMineQueue());
                }
            }
            record.setIdleMineQueueList(newQueues);
            record = idleMineRepository.save(record);
            return record;
        }
        return null;
    }

    public IdleMineRecord shutdown(long accountId, int index) {
        checkPlayerLevel(accountId);
        if (index < 1 || index > IdleMineConstants.MAX_MINE_QUEUE_COUNT) {
            throw IdleMineException.emptyIndex();
        }
        Optional<IdleMineRecord> orecord = idleMineRepository.findByAccountIdForWrite(accountId);
        if (orecord.isPresent()) {
            IdleMineRecord record = orecord.get();
            List<IdleMineQueue> queues = record.getIdleMineQueueList();
            List<IdleMineQueue> newQueues = new ArrayList<>();
            for (int i = 0; i < queues.size(); i++) {
                if (i == index - 1) {
                    newQueues.add(new IdleMineQueue());
                } else {
                    newQueues.add(queues.get(i));
                }
            }
            record.setIdleMineQueueList(newQueues);
            record = idleMineRepository.save(record);
            return record;
        }
        return null;
    }

    public IdleMineRecord take(long accountId) {
        checkPlayerLevel(accountId);
        Optional<IdleMineRecord> orecord = idleMineRepository.findByAccountIdForWrite(accountId);
        if (orecord.isPresent()) {
            IdleMineRecord record = orecord.get();
            List<IdleMineReward> rewards = record.getIdleMineRewardList();
            if (rewards.isEmpty()) {
                throw IdleMineException.emptyReward();
            }
            rewards.forEach((reward) -> {
                currencyService.increaseCurrency(accountId, reward.getCurrencyId(), reward.getCurrencyAmount());
            });
            record.clearIdleMineReward();
            record = idleMineRepository.save(record);
            return record;
        }
        return null;
    }

    public IdleMineRecord expand(long accountId) {
        checkPlayerLevel(accountId);
        Optional<IdleMineRecord> orecord = idleMineRepository.findByAccountIdForWrite(accountId);
        if (orecord.isPresent()) {
            IdleMineRecord record = orecord.get();
            if (record.getAvailableMineQueueCount() >= IdleMineConstants.MAX_MINE_QUEUE_COUNT) {
                throw IdleMineException.insufficientMineQueueCount();
            }
            long costMilliKc = record.getAvailableMineQueueCount() == 1 ? IdleMineConstants.FIRST_EXPAND_MINE_QUEUE_MILLIXS_COST : IdleMineConstants.SECOND_EXPAND_MINE_QUEUE_MILLIXS_COST;
            if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_毫仙石).getAmount() < costMilliKc) {
                throw IdleMineException.insufficientCurrency();
            }
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, costMilliKc, true, CurrencyConstants.PURPOSE_DECREMENT_三界经商提升经商位上限);
            record.setAvailableMineQueueCount(record.getAvailableMineQueueCount() + 1);
            record = idleMineRepository.save(record);
            return record;
        }
        return null;
    }

    private IdleMineRecord findOrCreate(long accountId) {
        IdleMineRecord record = idleMineRepository.findByAccountId(accountId);
        if (record == null) {
            record = new IdleMineRecord();
            record.setAccountId(accountId);
            record.setAvailableMineQueueCount(1);
            record = idleMineRepository.save(record);
        }
        return record;
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < IdleMineConstants.PLAYER_LEVEL_REQUIRE) {
            throw IdleMineException.insufficientPlayerLevel();
        }
    }

    private long secondToMilliSecond(long second) {
        return second * 1000;
    }

}
