/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import java.util.List;
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
@RequestMapping("/idleMine")
public class IdleMineController implements ModuleApiProvider {

    @Autowired
    IdleMineService idleMineService;

    @RequestMapping("/get")
    public IdleMineRecord get(@AuthenticationPrincipal Account account) {
        return idleMineService.get(account.getId());
    }

    @RequestMapping("/price")
    public List<IdleMinePrice> price(@AuthenticationPrincipal Account account) {
        return idleMineService.price(account.getId());
    }

    @PostMapping("/hire")
    public IdleMineRecord hire(
            @AuthenticationPrincipal Account account,
            @RequestParam("teamId") long teamId,
            @RequestParam("mapId") long mapId,
            @RequestParam("activePointsToUse") long activePointsToUse,
            @RequestParam("expectedPrice") long expectedPrice) {
        return idleMineService.hire(account.getId(), teamId, mapId, activePointsToUse, expectedPrice);
    }

    @PostMapping("/balance")
    public IdleMineRecord balance(@AuthenticationPrincipal Account account) {
        return idleMineService.balance(account.getId());
    }

    @PostMapping("/shutdown")
    public IdleMineRecord shutdown(
            @AuthenticationPrincipal Account account,
            @RequestParam("index") int index) {
        return idleMineService.shutdown(account.getId(), index);
    }

    @PostMapping("/take")
    public IdleMineRecord take(@AuthenticationPrincipal Account account) {
        return idleMineService.take(account.getId());
    }

    @PostMapping("/expand")
    public IdleMineRecord expand(@AuthenticationPrincipal Account account) {
        return idleMineService.expand(account.getId());
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .name("三界经商")
                .baseUri("/idleMine")
                //
                .webInterface()
                .uri("/get")
                .description("查看角色的记录")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .and()
                //
                .webInterface()
                .uri("/price")
                .description("查看所有地图的雇佣价格")
                .responseArray(IdleMinePrice.class, "地图的价格列表")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .and()
                //
                .webInterface()
                .uri("/hire")
                .post()
                .description("雇佣一支商队")
                .requestParameter("long", "teamId", "商队id")
                .requestParameter("long", "mapId", "地图id")
                .requestParameter("long", "activePointsToUse", "要使用的活跃点")
                .requestParameter("long", "expectedPrice", "当前的货币消耗价")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(IdleMineException.EC_INSUFFICIENT_CURRENCY, "拥有的货币不足")
                .expectableError(IdleMineException.EC_INSUFFICIENT_MINE_QUEUE_COUNT, "空闲的经商位不足")
                .and()
                //
                .webInterface()
                .uri("/balance")
                .post()
                .description("结算所有商队的挂机奖励")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .and()
                //
                .webInterface()
                .uri("/shutdown")
                .post()
                .description("遣散一支商队")
                .requestParameter("int", "index", "商队所在的经商位的编号，1~3")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(IdleMineException.EC_EMPTY_INDEX, "该经商位上没有商队")
                .and()
                //
                .webInterface()
                .uri("/take")
                .post()
                .description("领取储物箱的奖励")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(IdleMineException.EC_EMPTY_REWARD, "储物箱为空")
                .and()
                //
                .webInterface()
                .uri("/expand")
                .post()
                .description("扩充经商位")
                .response(IdleMineRecord.class, "数据库实体")
                .expectableError(IdleMineException.EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足")
                .expectableError(IdleMineException.EC_INSUFFICIENT_CURRENCY, "拥有的货币不足")
                .expectableError(IdleMineException.EC_MINE_QUEUE_COUNT_MAX, "经商位已达上限")
                .and();
    }

}
