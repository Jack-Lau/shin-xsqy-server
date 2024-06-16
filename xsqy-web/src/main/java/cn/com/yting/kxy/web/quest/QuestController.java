/*
 * Created 2018-8-3 11:51:31
 */
package cn.com.yting.kxy.web.quest;

import java.util.List;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/quest")
public class QuestController implements ModuleApiProvider {

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private QuestService questService;
    @Autowired
    private WebsocketMessageService websocketMessageService;

    @RequestMapping("/view/myself")
    public List<QuestRecord> viewMyselfAll(@AuthenticationPrincipal Account account) {
        return questRepository.findByAccountId(account.getId());
    }

    @RequestMapping("/view/myself/{questId}")
    public QuestRecord viewMyself(
        @AuthenticationPrincipal Account account,
        @PathVariable("questId") long questId
    ) {
        return questRepository.findById(account.getId(), questId)
            .orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/action/myself/{questId}/start")
    public QuestRecord startQuest(
        @AuthenticationPrincipal Account account,
        @PathVariable("questId") long questId
    ) {
        return questService.startQuest(account.getId(), questId);
    }

    @PostMapping("/action/myself/{questId}/achieveObjective")
    public QuestRecord achieveObjective(
        @AuthenticationPrincipal Account account,
        @PathVariable("questId") long questId,
        @RequestParam("objectiveIndex") int objectiveIndex,
        @RequestParam(name = "args", defaultValue = "") String args
    ) {
        return questService.achieveObjective(account.getId(), questId, objectiveIndex, args.split(","));
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    public void triggerDailyReset() {
        questService.anyReset(ResetType.DAILY);
    }

    @TransactionalEventListener
    public void onQuestStarted(QuestStartedEvent event) {
        QuestRecord questRecord = event.getQuestRecord();
        websocketMessageService.sendToUser(questRecord.getAccountId(), "/quest/started", questRecord);
    }

    @TransactionalEventListener
    public void onQuestCompleted(QuestCompletedEvent event) {
        QuestRecord questRecord = event.getQuestRecord();
        websocketMessageService.sendToUser(questRecord.getAccountId(), "/quest/completed", questRecord);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("quest")
            .baseUri("/quest")
            //
            .webInterface()
            .name("viewMyselfAll")
            .uri("/view/myself")
            .description("获得自己的所有的任务记录")
            .responseArray(QuestRecord.class, "任务记录的列表")
            .and()
            //
            .webInterface()
            .name("viewMyself")
            .uri("/view/myself/{questId}")
            .description("获得自己的指定的任务记录")
            .requestParameter("integer", "questId", "任务的id")
            .response(QuestRecord.class, "对应的任务记录")
            .and()
            //
            .webInterface()
            .name("start")
            .uri("/action/myself/{questId}/start")
            .post()
            .description("开始一个任务")
            .requestParameter("integer", "questId", "任务的id")
            .response(QuestRecord.class, "对应的任务记录")
            .expectableError(KxyWebException.EC_UNKNOW, "任务已经开始")
            .expectableError(KxyWebException.EC_UNKNOW, "未达到等级要求")
            .expectableError(KxyWebException.EC_UNKNOW, "未达到前置条件")
            .and()
            //
            .webInterface()
            .name("achieveObjective")
            .uri("/action/myself/{questId}/achieveObjective")
            .post()
            .description("完成一个任务目标")
            .requestParameter("integer", "questId", "任务的id")
            .requestParameter("integer", "objectiveIndex", "任务目标在任务定义中的索引")
            .requestParameter("string", "args", "逗号分隔的额外参数列表（可选）")
            .response(QuestRecord.class, "对应的任务记录")
            .expectableError(KxyWebException.EC_UNKNOW, "任务目标已经完成")
            .and()
            //
            .webInterface()
            .uri("/triggerDailyReset")
            .post()
            .description("触发每日重置")
            .and()
            //
            //
            //
            .webNotification()
            .queue("/quest/started")
            .description("开始了新任务的通知")
            .messageType(QuestRecord.class)
            .and()
            //
            .webNotification()
            .queue("/quest/completed")
            .description("任务完成的通知")
            .messageType(QuestRecord.class)
            .and();
    }
}
