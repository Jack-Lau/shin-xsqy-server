/*
 * Created 2018-8-4 17:46:56
 */
package cn.com.yting.kxy.web.quest;

import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/management/quest")
public class QuestManagementController implements ModuleApiProvider {

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private QuestService questService;

    @PostMapping("/triggerReset")
    public WebMessageWrapper triggerReset(
        @RequestParam("resetType") ResetType resetType
    ) {
        questService.anyReset(resetType);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/tryPickupForAll")
    public WebMessageWrapper tryPickupForAll(
        @RequestParam("questId") long questId
    ) {
        questRepository.findAccountIdsByNotStartedQuest(questId).forEach(id -> questService.tryStartQuest(id, questId, false));
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("任务管理")
            .baseUri("/management/quest")
            //
            .webInterface()
            .uri("/triggerReset")
            .post()
            .description("触发重置")
            .requestParameter("string", "resetType", "重置类型")
            .and()
            //
            .webInterface()
            .uri("/tryPickupForAll")
            .post()
            .description("为所有玩家尝试领取一个任务")
            .requestParameter("integer", "questId", "任务的 id")
            .and();
    }
}
