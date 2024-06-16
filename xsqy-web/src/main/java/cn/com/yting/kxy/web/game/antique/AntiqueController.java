/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
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
@RequestMapping("/antique")
public class AntiqueController implements ModuleApiProvider {

    @Autowired
    AntiqueService antiqueService;

    @RequestMapping("/get")
    public AntiqueOverall get(@AuthenticationPrincipal Account account) {
        return antiqueService.get(account.getId());
    }

    @PostMapping("/buy")
    public AntiqueOverall buy(@AuthenticationPrincipal Account account) {
        return antiqueService.buy(account.getId());
    }

    @PostMapping("/sell")
    public AntiqueOverall sell(@AuthenticationPrincipal Account account) {
        return antiqueService.sell(account.getId());
    }

    @PostMapping("/repair")
    public AntiqueOverall repair(
            @AuthenticationPrincipal Account account,
            @RequestParam("part") String part) {
        return antiqueService.repair(account.getId(), part);
    }

    @PostMapping("/take")
    public AntiqueOverall take(@AuthenticationPrincipal Account account) {
        return antiqueService.take(account.getId());
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .name("西域商人")
                .baseUri("/antique")
                //
                .webInterface()
                .uri("/get")
                .description("查看古董记录")
                .response(AntiqueOverall.class, "古董记录，包含角色本身的记录和服务器记录")
                .expectableError(AntiqueException.EC_NOT_STARTED_YET, "活动未开启")
                .expectableError(AntiqueException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .and()
                //
                .webInterface()
                .uri("/buy")
                .post()
                .description("购买一个古董")
                .response(AntiqueOverall.class, "古董记录，包含角色本身的记录和服务器记录")
                .expectableError(AntiqueException.EC_NOT_STARTED_YET, "活动未开启")
                .expectableError(AntiqueException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(AntiqueException.EC_INSUFFICIENT_CURRENCY, "拥有的货币不足")
                .expectableError(AntiqueException.EC_ALREADY_IN_REPAIR, "已经在修复一个古董")
                .and()
                //
                .webInterface()
                .uri("/sell")
                .post()
                .description("出售一个古董")
                .response(AntiqueOverall.class, "古董记录，包含角色本身的记录和服务器记录")
                .expectableError(AntiqueException.EC_NOT_STARTED_YET, "活动未开启")
                .expectableError(AntiqueException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(AntiqueException.EC_DO_NOT_HAVE_ANTIQUE, "当前没拥有古董")
                .and()
                //
                .webInterface()
                .uri("/repair")
                .post()
                .description("修复一个古董")
                .response(AntiqueOverall.class, "古董记录，包含角色本身的记录和服务器记录")
                .requestParameter("string", "part", "假如这次修复成功的话，古董的部位状态记录")
                .expectableError(AntiqueException.EC_NOT_STARTED_YET, "活动未开启")
                .expectableError(AntiqueException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(AntiqueException.EC_DO_NOT_HAVE_ANTIQUE, "当前没拥有古董")
                .expectableError(AntiqueException.EC_ANTIQUE_LEVEL_MAX, "古董已达最高修复等级")
                .expectableError(AntiqueException.EC_INSUFFICIENT_CURRENCY, "拥有的货币不足")
                .and()
                //
                .webInterface()
                .uri("/take")
                .post()
                .description("领取全服奖励")
                .response(AntiqueOverall.class, "古董记录，包含角色本身的记录和服务器记录")
                .expectableError(AntiqueException.EC_NOT_STARTED_YET, "活动未开启")
                .expectableError(AntiqueException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(AntiqueException.EC_AWARD_TAKE_COUNT_MAX, "今日已达到领取次数上限")
                .expectableError(AntiqueException.EC_INSUFFICIENT_AWARD_REMAIN, "全服奖励的剩余数量不足")
                .expectableError(AntiqueException.EC_AWARD_TAKEN, "该全服奖励已经领过了")
                .and();
    }

}
