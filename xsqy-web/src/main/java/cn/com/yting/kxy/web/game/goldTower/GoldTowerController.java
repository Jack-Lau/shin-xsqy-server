/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.ranking.RankingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/goldTower")
public class GoldTowerController implements ModuleApiProvider {

    @Autowired
    GoldTowerService goldTowerService;

    @RequestMapping("/getGoldTowerRecord")
    public GoldTowerRecord getGoldTowerRecord(@AuthenticationPrincipal Account account) {
        return goldTowerService.getGoldTowerRecord(account.getId());
    }

    @RequestMapping("/getGoldTowerStatus")
    public GoldTowerStatusEntity getGoldTowerStatus(@AuthenticationPrincipal Account account) {
        return goldTowerService.getGoldTowerStatusEntity();
    }

    @RequestMapping("/getGoldTowerChallenge")
    public GoldTowerChallengeEntity getGoldTowerChallenge(@AuthenticationPrincipal Account account) {
        return goldTowerService.getGoldTowerChallengeEntity(account.getId());
    }

    @RequestMapping("/getGoldTowerRoom")
    public GoldTowerRoomEntity getGoldTowerRoom(
            @AuthenticationPrincipal Account account,
            @RequestParam("roomId") long roomId) {
        return goldTowerService.getGoldTowerRoomEntity(roomId);
    }

    @PostMapping("/startOrReturnGoldTowerChallenge")
    public GoldTowerChallengeEntity startOrReturnGoldTowerChallenge(@AuthenticationPrincipal Account account) {
        return goldTowerService.startOrReturnGoldTowerChallenge(account.getId());
    }

    @PostMapping("/startWipeOutBattle")
    public BattleResponse startWipeOutBattle(@AuthenticationPrincipal Account account) {
        return goldTowerService.startWipeOutBattle(account.getId());
    }

    @PostMapping("/tryFinishWipeOutBattle")
    public boolean tryFinishWipeOutBattle(@AuthenticationPrincipal Account account) {
        return goldTowerService.tryFinishWipeOutBattle(account.getId());
    }

    @PostMapping("/upToTargetFloor")
    public GoldTowerWipeOut upToTargetFloor(@AuthenticationPrincipal Account account) {
        return goldTowerService.upToTargetFloor(account.getId());
    }

    @PostMapping("/takeWipeOutAward")
    public GoldTowerChallengeEntity takeWipeOutAward(@AuthenticationPrincipal Account account) {
        return goldTowerService.takeWipeOutAward(account.getId());
    }

    @PostMapping("/startGoldTowerBattle")
    public BattleResponse startGoldTowerBattle(@AuthenticationPrincipal Account account) {
        return goldTowerService.startGoldTowerBattle(account.getId());
    }

    @PostMapping("/tryFinishGoldTowerChallenge")
    public GoldTowerChallengeEntity tryFinishGoldTowerChallenge(
            @AuthenticationPrincipal Account account,
            @RequestParam("param") String param) {
        return goldTowerService.tryFinishGoldTowerChallenge(account.getId(), param);
    }

    @PostMapping("/openTreasure")
    public GoldTowerChallengeEntity openTreasure(@AuthenticationPrincipal Account account) {
        return goldTowerService.openTreasure(account.getId());
    }

    @PostMapping("/gotoNextRoom")
    public GoldTowerChallengeEntity gotoNextRoom(
            @AuthenticationPrincipal Account account,
            @RequestParam("waypoint") int waypoint) {
        return goldTowerService.gotoNextRoom(account.getId(), waypoint);
    }

    @PostMapping("/getRanking")
    public RankingInfo getRanking(@AuthenticationPrincipal Account account) {
        return goldTowerService.getGoldTowerRankingInfo(account.getId());
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .name("金光塔")
                .baseUri("/goldTower")
                //
                .webInterface()
                .uri("/getGoldTowerStatus")
                .description("查看当日的金光塔概况")
                .response(GoldTowerStatusEntity.class, "金光塔概况")
                .and()
                //
                .webInterface()
                .uri("/getGoldTowerChallenge")
                .description("查看角色的金光塔挑战记录")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .and()
                //
                .webInterface()
                .uri("/getGoldTowerRoom")
                .description("查看金光塔的一个指定房间")
                .requestParameter("long", "roomId", "房间id")
                .response(GoldTowerRoomEntity.class, "房间详情")
                .and()
                //
                .webInterface()
                .uri("/startOrReturnGoldTowerChallenge")
                .post()
                .description("开始/返回一个金光塔挑战")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_CHALLENGE_COUNT, "挑战次数不足")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .and()
                //
                .webInterface()
                .uri("/startGoldTowerBattle")
                .post()
                .description("开启一场金光塔战斗")
                .response(BattleResponse.class, "包含战斗结果的响应")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .and()
                //
                .webInterface()
                .uri("/tryFinishGoldTowerChallenge")
                .post()
                .description("尝试完成当前房间的挑战")
                .requestParameter("string", "param", "挑战参数")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_CURRENCY, "需要的货币不足")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .and()
                //
                .webInterface()
                .uri("/openTreasure")
                .post()
                .description("领取宝箱奖励")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .and()
                //
                .webInterface()
                .uri("/gotoNextRoom")
                .post()
                .description("通过传送点前往下一个房间")
                .requestParameter("int", "waypoint", "传送点序号")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .and()
                //
                .webInterface()
                .uri("/getRanking")
                .post()
                .description("查看金光塔排行榜")
                .response(RankingInfo.class, "排行榜记录，包含前100名的列表和自身")
                .and()
                //
                .webInterface()
                .uri("/getGoldTowerRecord")
                .description("查看角色的金光塔历史和扫荡记录")
                .response(GoldTowerRecord.class, "历史记录")
                .and()
                //
                .webInterface()
                .uri("/startWipeOutBattle")
                .post()
                .description("开启一场扫荡战斗")
                .response(BattleResponse.class, "包含战斗结果的响应")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .expectableError(GoldTowerException.EC_NOT_AT_FLOOR_ZERO, "当前不在第0层")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_CHALLENGE_COUNT, "挑战次数不足")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_MAX_FINISH_FLOOR, "历史最高通过层数不足")
                .and()
                //
                .webInterface()
                .uri("/tryFinishWipeOutBattle")
                .post()
                .description("尝试结算一场扫荡战斗")
                .response(Boolean.class, "胜利为true，失败为false")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_MAX_FINISH_FLOOR, "历史最高通过层数不足")
                .and()
                //
                .webInterface()
                .uri("/upToTargetFloor")
                .post()
                .description("前往扫荡目标楼层")
                .response(GoldTowerWipeOut.class, "扫荡集成信息")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_MAX_FINISH_FLOOR, "历史最高通过层数不足")
                .expectableError(GoldTowerException.EC_CANNOT_FAST_UP, "不满足快速传送的条件")
                .and()
                //
                .webInterface()
                .uri("/takeWipeOutAward")
                .post()
                .description("领取扫荡奖励")
                .response(GoldTowerChallengeEntity.class, "挑战记录")
                .expectableError(GoldTowerException.EC_NOT_IN_CHALLENGE, "金光塔已重置")
                .expectableError(GoldTowerException.EC_INSUFFICIENT_MAX_FINISH_FLOOR, "历史最高通过层数不足")
                .expectableError(GoldTowerException.EC_CANNOT_TAKE_WIPE_OUT_AWARD, "不满足领取扫荡奖励的条件")
                .and();
    }

}
