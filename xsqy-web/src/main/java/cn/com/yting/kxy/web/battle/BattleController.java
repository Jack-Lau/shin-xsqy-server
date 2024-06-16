/*
 * Created 2018-8-15 11:57:05
 */
package cn.com.yting.kxy.web.battle;

import java.util.Collections;

import cn.com.yting.kxy.battle.BattleResult.TurnInfo;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/battle")
public class BattleController implements ModuleApiProvider {

    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private BattleService battleService;

    @PostMapping("/start")
    public BattleResponse startBattle(
            @AuthenticationPrincipal Account account,
            @RequestParam("battleDescriptorId") long battleDescriptorId,
            @RequestParam(name = "oneshot", defaultValue = "true") boolean oneshot
    ) {
        BattleSession battleSession = battleService.startAsyncPVE(
                account.getId(),
                battleDescriptorId,
                oneshot,
                false,
                0,
                Collections.emptyList()
        );
        return new BattleResponse(battleSession.getId(), battleSession.getBattleDirector().getBattleResult());
    }

    @RequestMapping("/view/{id}")
    public BattleResponse getBattle(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long battleSessionId
    ) {
        BattleSession battleSession = battleRepository.findById(battleSessionId)
                .orElseThrow(ControllerUtils::notFoundException);
        if (battleSession.getAccountId() != account.getId()) {
            throw KxyWebException.unknown("不是自己的战斗");
        }
        return new BattleResponse(battleSessionId, battleSession.getBattleDirector().getBattleResult());
    }

    @PostMapping("/action/{id}/nextTurn")
    public TurnInfo nextTurn(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long battleSessionId,
            @RequestParam("turnCount") int turnCount,
            @RequestParam(name = "skillId", required = false) Long skillId,
            @RequestParam(name = "targetId", required = false) Long targetId
    ) {
        return battleService.nextTurn(account.getId(), battleSessionId, turnCount, skillId, targetId);
    }

    @PostMapping("/clean")
    public WebMessageWrapper clean(@AuthenticationPrincipal Account account) {
        battleService.clean(account.getId());
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("battle")
                .baseUri("/battle")
                //
                .webInterface()
                .uri("/start")
                .post()
                .description("开启一场战斗")
                .requestParameter("integer", "battleDescriptorId", "战斗配置的id")
                .requestParameter("boolean", "oneshot", "是否直接进行到结束")
                .response(BattleResponse.class, "包含战斗结果的响应")
                .and()
                //
                .webInterface()
                .name("view")
                .uri("/view/{id}")
                .description("获得一个战斗会话的信息")
                .requestParameter("integer", "id", "战斗会话的id")
                .response(BattleResponse.class, "包含战斗结果的响应")
                .and()
                //
                .webInterface()
                .name("nextTurn")
                .uri("/action/{id}/nextTurn")
                .post()
                .description("进行下一回合")
                .requestParameter("integer", "id", "战斗会话的id")
                .requestParameter("integer", "turnCount", "回合数")
                .requestParameter("integer", "skillId", "技能id（可选）")
                .requestParameter("integer", "targetId", "目标id（可选）")
                .response(TurnInfo.class, "回合记录")
                .and()
                //
                .webInterface()
                .uri("/clean")
                .post()
                .description("清理未结束的战斗");
    }
}
