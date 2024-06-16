/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.game.baccarat.BaccaratConstants.Status;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import lombok.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class BaccaratService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;

    @Autowired
    BaccaratGameRepository baccaratGameRepository;
    @Autowired
    BaccaratBetRepository baccaratBetRepository;

    @Autowired
    TimeProvider timeProvider;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    TaskScheduler taskScheduler;

    boolean AVAILABLE = false;
    Status currentStatus = Status.WAIT;
    long currentGameId = 0;
    List<Long> currentBet = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L);
    List<Broadcast> currentBroadcast = new ArrayList<>();
    List<BaccaratGame> gameRecords = new ArrayList<>();
    Future betFuture;
    Future lotteryFuture;
    Future broadcastFuture;

    RandomSelector<Long> cardShoe;

    @Override
    public void afterPropertiesSet() throws Exception {
        //
        open();
    }

    public void open() {
        if (!AVAILABLE) {
            AVAILABLE = true;
            currentStatus = Status.WAIT;
            currentGameId = baccaratGameRepository.count() < 1 ? 0 : baccaratGameRepository.findAll().get((int) (baccaratGameRepository.count() - 1)).getId();
            currentBet = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L);
            betFuture = taskScheduler.schedule(() -> startBet(), new CronTrigger(BaccaratConstants.BET_CRON));
            lotteryFuture = taskScheduler.schedule(() -> startLottery(), new CronTrigger(BaccaratConstants.LOTTERY_CRON));
            broadcastFuture = taskScheduler.schedule(() -> broadcast(), new CronTrigger(BaccaratConstants.BROADCAST_CRON));
            //
            RandomSelectorBuilder cardShoeBuilder = RandomSelector.<Long>builder();
            for (long i = 1; i <= 13; i++) {
                cardShoeBuilder.add(i, 1);
            }
            cardShoe = cardShoeBuilder.build(RandomSelectType.DEPENDENT);
        }
    }

    public void close() {
        if (AVAILABLE) {
            AVAILABLE = false;
            if (betFuture != null) {
                betFuture.cancel(true);
            }
            if (lotteryFuture != null) {
                lotteryFuture.cancel(true);
            }
            if (broadcastFuture != null) {
                broadcastFuture.cancel(true);
            }
            if (currentStatus == Status.BET) {
                startLottery();
            }
        }
    }

    public BaccaratOverall getOverall(long accountId) {
        return new BaccaratOverall(
                AVAILABLE,
                currentStatus,
                currentGameId,
                currentBet,
                baccaratBetRepository.findByAccountIdAndGameId(accountId, currentGameId),
                baccaratGameRepository.findById(currentGameId).orElse(null));
    }

    public BaccaratGame getBaccaratGameById(long gameId) {
        return baccaratGameRepository.findById(gameId).orElse(null);
    }

    public List<BaccaratGame> getBaccaratGameRecords() {
        return gameRecords;
    }

    public BaccaratBet getBaccaratBetByAccountIdAndGameId(long accountId, long gameId) {
        return baccaratBetRepository.findByAccountIdAndGameId(accountId, gameId);
    }

    public List<BaccaratBet> getBaccaratBetsByAccountId(long accountId) {
        return baccaratBetRepository.findByAccountId(accountId);
    }

    public BaccaratOverall bet(long accountId, int betIndex, long amount) {
        checkAvailable();
        checkPlayerLevel(accountId);
        if (currentStatus != Status.BET) {
            throw BaccaratException.不是下注阶段();
        }
        if (betIndex < 0 || betIndex > 5 || amount < 1) {
            throw BaccaratException.不是下注阶段();
        }
        BaccaratBet baccaratBet = findOrCreate(accountId, currentGameId);
        if (amount + baccaratBet.getBetsSum() > BaccaratConstants.BET_LIMIT) {
            throw BaccaratException.超出单局投注限制();
        }
        if (amount != 40_000 && amount != 200_000 && amount != 1000_000 && amount != 5000_000) {
            throw BaccaratException.超出单局投注限制();
        }
        //
        currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, amount, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒下注);
        currentBet.set(betIndex, currentBet.get(betIndex) + amount);
        baccaratBet.addBet(betIndex, amount);
        baccaratBetRepository.save(baccaratBet);
        //
        return getOverall(accountId);
    }

    public BaccaratOverall unBet(long accountId, int betIndex) {
        checkAvailable();
        checkPlayerLevel(accountId);
        if (currentStatus != Status.BET) {
            throw BaccaratException.不是下注阶段();
        }
        if (betIndex < 0 || betIndex > 5) {
            throw BaccaratException.不是下注阶段();
        }
        BaccaratBet baccaratBet = findOrCreate(accountId, currentGameId);
        List<Long> bets = baccaratBet.getBets();
        if (bets.get(betIndex) > 0) {
            currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bets.get(betIndex), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
            currentBet.set(betIndex, currentBet.get(betIndex) - bets.get(betIndex));
            baccaratBet.addBet(betIndex, -bets.get(betIndex));
            baccaratBetRepository.save(baccaratBet);
        }
        //
        return getOverall(accountId);
    }

    private void startBet() {
        if (currentStatus == Status.WAIT) {
            currentStatus = Status.BET;
            currentBet = Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L);
            currentGameId++;
        }
    }

    private void startLottery() {
        if (currentStatus == Status.BET) {
            currentStatus = Status.WAIT;
            //
            BaccaratGame baccaratGame = new BaccaratGame();
            baccaratGame.setRedPoint_1(cardShoe.getSingle());
            baccaratGame.setRedPoint_2(cardShoe.getSingle());
            baccaratGame.setBluePoint_1(cardShoe.getSingle());
            baccaratGame.setBluePoint_2(cardShoe.getSingle());
            baccaratGame.setLotteryTime(new Date(timeProvider.currentTime()));
            baccaratGame = baccaratGameRepository.save(baccaratGame);
            gameRecords.add(baccaratGame);
            if (gameRecords.size() > 30) {
                gameRecords.remove(0);
            }
            long red = (getActuralPoint(baccaratGame.getRedPoint_1()) + getActuralPoint(baccaratGame.getRedPoint_2())) % 10;
            long blue = (getActuralPoint(baccaratGame.getBluePoint_1()) + getActuralPoint(baccaratGame.getBluePoint_2())) % 10;
            //
            List<BaccaratBet> baccaratBets = baccaratBetRepository.findByGameId(currentGameId);
            for (BaccaratBet baccaratBet : baccaratBets) {
                long totalGain = 0;
                if (baccaratBet.getBet_0() > 0) {
                    long bet = baccaratBet.getBet_0();
                    double rate = 1.9;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (red == blue) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    } else {
                        if (red > blue) {
                            currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                            totalGain += (long) (bet * rate);
                        } else {
                            totalGain -= bet;
                        }
                    }
                }
                //
                if (baccaratBet.getBet_1() > 0) {
                    long bet = baccaratBet.getBet_1();
                    double rate = 1.9;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (red == blue) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    } else {
                        if (red < blue) {
                            currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                            totalGain += (long) (bet * rate);
                        } else {
                            totalGain -= bet;
                        }
                    }
                }
                //
                if (baccaratBet.getBet_2() > 0) {
                    long bet = baccaratBet.getBet_2();
                    double rate = 8.55;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (red == blue) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                        totalGain += (long) (bet * rate);
                    } else {
                        totalGain -= bet;
                    }
                }
                //
                if (baccaratBet.getBet_3() > 0) {
                    long bet = baccaratBet.getBet_3();
                    double rate = 17.1;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (baccaratGame.getRedPoint_1() == baccaratGame.getRedPoint_2() && baccaratGame.getRedPoint_1() < 10) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                        totalGain += (long) (bet * rate);
                    } else {
                        totalGain -= bet;
                    }
                }
                //
                if (baccaratBet.getBet_4() > 0) {
                    long bet = baccaratBet.getBet_4();
                    double rate = 17.1;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (baccaratGame.getBluePoint_1() == baccaratGame.getBluePoint_2() && baccaratGame.getBluePoint_1() < 10) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                        totalGain += (long) (bet * rate);
                    } else {
                        totalGain -= bet;
                    }
                }
                //
                if (baccaratBet.getBet_5() > 0) {
                    long bet = baccaratBet.getBet_5();
                    double rate = 102.6;
                    currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒返还下注);
                    currencyService.decreaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, bet, true, CurrencyConstants.PURPOSE_DECREMENT_欢乐筒筒支付);
                    if (baccaratGame.getRedPoint_1() >= 10
                            && baccaratGame.getRedPoint_2() >= 10
                            && baccaratGame.getBluePoint_1() >= 10
                            && baccaratGame.getBluePoint_2() >= 10) {
                        currencyService.increaseCurrency(baccaratBet.getAccountId(), CurrencyConstants.ID_毫仙石, (long) (bet * rate), CurrencyConstants.PURPOSE_INCREMENT_欢乐筒筒胜利);
                        totalGain += (long) (bet * rate);
                    } else {
                        totalGain -= bet;
                    }
                }
                //
                baccaratBet.setTotalGain(totalGain);
                baccaratBetRepository.save(baccaratBet);
                eventPublisher.publishEvent(new BaccaratLotteryEvent(this, baccaratBet));
                //
                if (totalGain > BaccaratConstants.BROADCAST_LOTTERY) {
                    currentBroadcast.add(new Broadcast(baccaratBet.getAccountId(), KuaibiUnits.toKuaibi(totalGain)));
                }
            }
        }
    }

    private void broadcast() {
        currentBroadcast.forEach((broadcast) -> {
            String playerName = compositePlayerService.getPlayerBaseInfo(broadcast.getAccountId()).getPlayer().getPlayerName();
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            3200057,
                            ImmutableMap.of(
                                    "playerName", playerName,
                                    "amount", broadcast.getAmount()
                            )
                    )
            );
            chatService.offerInterestingMessage(
                    3290005,
                    ChatMessage.createTemplateMessage(
                            3290005,
                            ImmutableMap.of(
                                    "playerName", playerName,
                                    "amount", broadcast.getAmount()
                            )
                    )
            );
        });
        currentBroadcast.clear();
    }

    private BaccaratBet findOrCreate(long accountId, long gameId) {
        BaccaratBet baccaratBet = baccaratBetRepository.findByAccountIdAndGameIdForWrite(accountId, gameId).orElse(null);
        if (baccaratBet == null) {
            baccaratBet = new BaccaratBet();
            baccaratBet.setAccountId(accountId);
            baccaratBet.setGameId(gameId);
            baccaratBet.setCreateTime(new Date(timeProvider.currentTime()));
            baccaratBetRepository.save(baccaratBet);
        }
        return baccaratBetRepository.findByAccountIdAndGameIdForWrite(accountId, gameId).orElse(null);
    }

    private void checkAvailable() {
        if (!AVAILABLE) {
            throw BaccaratException.活动未开启();
        }
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerLevel(accountId) < BaccaratConstants.PLAYER_LEVEL_REQUIRE) {
            throw BaccaratException.角色等级不足();
        }
    }

    private long getActuralPoint(long point) {
        return point >= 10 ? 0 : point;
    }

    @Value
    private class Broadcast {

        long accountId;
        long amount;

    }

}
