/*
 * Created 2018-12-18 19:08:39
 */
package cn.com.yting.kxy.web.battle.multiplayer;

import java.util.List;

import cn.com.yting.kxy.battle.BattleResult;
import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.QueueNotification;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/multiplayerBattle")
@ModuleDoc(moduleName = "multiplayer")
public class MultiplayerBattleController {

    @Autowired
    private MultiplayerBattleRepository multiplayerBattleRepository;

    @Autowired
    private MultiplayerBattleService multiplayerBattleService;

    @RequestMapping(path = "/{id}")
    @WebInterfaceDoc(description = "查看指定的战斗会话的战斗信息", response = "战斗信息")
    public BattleResult viewBattleInfo(
        @PathVariable("id") @ParamDoc("战斗会话的id") long sessionId
    ) {
        return multiplayerBattleRepository.findById(sessionId).orElseThrow(ControllerUtils::notFoundException)
            .getBattleDirector().getBattleResult();
    }

    @RequestMapping(path = "/{id}/viewSync")
    @WebInterfaceDoc(description = "获取同步信息", response = "")
    public SyncMessage viewSync(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("战斗会话的id") long sessionId
    ) {
        MultiplayerBattleSession session = multiplayerBattleRepository.findById(sessionId).orElseThrow(ControllerUtils::notFoundException);
        SyncMessage syncMessage = new SyncMessage();
        syncMessage.setSyncStatus(session.getSyncStatus());
        syncMessage.setSyncNumber(session.getSyncNumber());
        return syncMessage;
    }

    @PostMapping(path = "/{id}/sync", consumes = MediaType.APPLICATION_JSON_VALUE)
    @WebInterfaceDoc(description = "进行同步", response = "")
    public WebMessageWrapper sync(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("战斗会话的id") long sessionId,
        @RequestBody SyncMessage syncMessage
    ) {
        try {
            multiplayerBattleService.sync(account.getId(), sessionId, syncMessage);
            return WebMessageWrapper.ok();
        } catch (RuntimeException ex) {
            throw new MultiplayerBattleException(MultiplayerBattleException.EC_失去同步, "失去同步");
        }
    }

    @RequestMapping(path = "/attendingSessionIds")
    @WebInterfaceDoc(description = "查询自己正在参加的多人战斗", response = "战斗会话的id")
    public List<Long> findAttendingSessionIds(@AuthenticationPrincipal Account account) {
        return multiplayerBattleService.findAttendingSessionIds(account.getId());
    }

    @DebugOnly
    @PostMapping("/startBattle")
    @WebInterfaceDoc(description = "直接开始一场多人战斗（测试用）", response = "")
    public void startBattle(
        @RequestParam("redPartyAccountIds") @ParamDoc("要参加战斗的红队账号id的逗号分隔列表") String redPartyAccountIds,
        @RequestParam("bluePartyAccountIds") @ParamDoc("要参加战斗的蓝队账号id的逗号分隔列表") String bluePartyAccountIds
    ) {
        multiplayerBattleService.startBattle(
            CommaSeparatedLists.fromText(redPartyAccountIds, Long::valueOf),
            CommaSeparatedLists.fromText(bluePartyAccountIds, Long::valueOf),
            true,
            false,
            null
        );
    }

    @DebugOnly
    @PostMapping("/clean")
    @WebInterfaceDoc(description = "清理掉所有的战斗会话（测试用）", response = "")
    public void clean() {
        multiplayerBattleService.clean();
    }


    @QueueNotification(description = "多人战斗的同步通知", destination = "/multiplayerBattle/sync", messageType = SyncMessage.class)
    public void onSyncDummy() {
    }
}
