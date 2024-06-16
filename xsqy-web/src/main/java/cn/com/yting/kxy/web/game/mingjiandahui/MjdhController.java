/*
 * Created 2018-12-17 11:08:06
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.QueueNotification;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/mjdh")
@ModuleDoc(moduleName = "名剑大会")
public class MjdhController {

    @Autowired
    private MjdhSeasonRepository mjdhSeasonRepository;
    @Autowired
    private MjdhPlayerRepository mjdhPlayerRepository;
    @Autowired
    private MjdhWinnerRepository mjdhWinnerRepository;
    @Autowired
    private MjdhBattleLogRepository mjdhBattleLogRepository;

    @Autowired
    private MjdhService mjdhService;
    @Autowired
    private WebsocketMessageService websocketMessageService;

    @RequestMapping("/season/current")
    @WebInterfaceDoc(description = "查看当前赛季的信息", response = "赛季信息")
    public MjdhSeason viewCurrentSeason() {
        return mjdhSeasonRepository.findTopByOrderByIdDesc().get();
    }

    @RequestMapping("/season/current/detail")
    @WebInterfaceDoc(description = "查看当前赛季的详细信息", response = "赛季详细信息")
    public MjdhSeasonDetail viewCurrentSeasonDetail() {
        MjdhSeason mjdhSeason = mjdhSeasonRepository.findTopByOrderByIdDesc().get();
        int playerCount = mjdhPlayerRepository.countBySeasonId(mjdhSeason.getId());
        return new MjdhSeasonDetail(mjdhSeason, playerCount);
    }

    @RequestMapping("/player/myself")
    @WebInterfaceDoc(description = "查看自己当前赛季的玩家记录", response = "玩家记录")
    public MjdhPlayerRecord viewMyRecord(@AuthenticationPrincipal Account account) {
        return mjdhPlayerRepository.findById(mjdhService.getCurrentSeasonId(), account.getId())
            .orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/player/{accountId}")
    @WebInterfaceDoc(description = "查看指定账号当前赛季的玩家记录", response = "玩家记录")
    public MjdhPlayerRecord viewPlayerRecord(
        @PathVariable("accountId") @ParamDoc("要查询的账号id") long accountId
    ) {
        return mjdhPlayerRepository.findById(mjdhService.getCurrentSeasonId(), accountId)
            .orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/player/create")
    @WebInterfaceDoc(description = "创建自己的当前赛季的记录", response = "玩家记录")
    public MjdhPlayerRecord createMyRecord(@AuthenticationPrincipal Account account) {
        return mjdhService.createRecord(account.getId());
    }

    @RequestMapping("/winner/")
    @WebInterfaceDoc(description = "查看所有的胜者记录", response = "胜者记录")
    public List<MjdhWinnerRecord> viewWinner() {
        return mjdhWinnerRepository.findAll();
    }

    @RequestMapping("/winner/{seasonId}/{ranking}")
    @WebInterfaceDoc(description = "查看指定的赛季的指定排名的胜者记录", response = "胜者记录")
    public MjdhWinnerRecord viewWinnerById(
        @PathVariable("seasonId") @ParamDoc("赛季编号") long seasonId,
        @PathVariable("ranking") @ParamDoc("排名") int ranking
    ) {
        return mjdhWinnerRepository.findById(new MjdhWinnerRecord.PK(seasonId, ranking)).orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/startMatch")
    @WebInterfaceDoc(description = "开始匹配", response = "待定")
    public Object startMatch(@AuthenticationPrincipal Account account) {
        mjdhService.startMatch(account.getId());
        return WebMessageWrapper.ok();
    }

    @PostMapping("/cancelMatch")
    @WebInterfaceDoc(description = "取消匹配", response = "")
    public Object cancelMatch(@AuthenticationPrincipal Account account) {
        mjdhService.cancelMatch(account.getId());
        return WebMessageWrapper.ok();
    }

    @PostMapping("/player/myself/obtainDailyFirstWinAward")
    @WebInterfaceDoc(description = "领取每日首胜奖励", response = "奖励结果")
    public List<CurrencyStack> obtainDailyFirstWinAward(@AuthenticationPrincipal Account account) {
        return mjdhService.obtainDailyFirstWinAward(account.getId());
    }

    @PostMapping("/player/myself/obtainDailyConsecutiveWinAward")
    @WebInterfaceDoc(description = "领取每日连胜奖励", response = "奖励结果")
    public List<CurrencyStack> obtainDailyConsecutiveWinAward(@AuthenticationPrincipal Account account) {
        return mjdhService.obtainDailyConsecutiveWinAward(account.getId());
    }

    @PostMapping("/player/myself/obtainDailyTenBattleAward")
    @WebInterfaceDoc(description = "领取每日十胜奖励", response = "奖励结果")
    public List<CurrencyStack> obtainDailyTenBattleAward(@AuthenticationPrincipal Account account) {
        return mjdhService.obtainDailyTenBattleAward(account.getId());
    }

    @RequestMapping("/battleLog/mine/")
    @WebInterfaceDoc(description = "查看自己相关的战斗记录", response = "战斗记录")
    public List<MjdhBattleLog> viewBattleLogs(@AuthenticationPrincipal Account account) {
        final int limit = 20;
        return Stream.concat(
            mjdhBattleLogRepository.findByWinnerAccountIdOrderByEventTimeDesc(account.getId(), PageRequest.of(0, limit)).stream(),
            mjdhBattleLogRepository.findByLoserAccountIdOrderByEventTimeDesc(account.getId(), PageRequest.of(0, limit)).stream())
            .sorted(Comparator.comparing(MjdhBattleLog::getEventTime).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    @WebInterfaceDoc(description = "触发每日重置（测试用）", response = "")
    public void triggerDailyReset() {
        mjdhService.dailyReset();
    }

    @TransactionalEventListener
    @QueueNotification(destination = "/mjdh/singlePlayerBattleStarted", description = "名剑大会单人战斗开始的通知", messageType = BattleResponse.class)
    public void onSinglePlayerMjdhBattleStarted(SinglePlayerMjdhBattleStartedEvent event) {
        BattleSession session = event.getBattleSession();
        websocketMessageService.sendToUser(event.getAccountId(), "/mjdh/singlePlayerBattleStarted", event.getBattleSession().toBattleResponse());
    }

    @TransactionalEventListener
    @QueueNotification(destination = "/mjdh/multiplayerBattleStarted", description = "名剑大会多人战斗开始的通知", messageType = Void.class)
    public void onMultiplayerMjdhBattleStarted(MultiplayerMjdhBattleStartedEvent event) {
        event.getMultiplayerBattleSession().getAgents().forEach(agent -> {
            websocketMessageService.sendToUser(agent.getAccountId(), "/mjdh/multiplayerBattleStarted", WebMessageWrapper.ok());
        });
    }

    @TransactionalEventListener
    @QueueNotification(destination = "/mjdh/battleEnd", description = "名剑大会战斗结束的通知", messageType = MjdhBattleLog.class)
    public void onBattleEnd(MjdhBattleEndEvent event) {
        event.getAccountIds().forEach(accountId -> {
            websocketMessageService.sendToUser(accountId, "/mjdh/battleEnd", event.getBattleLog());
        });
    }
}
