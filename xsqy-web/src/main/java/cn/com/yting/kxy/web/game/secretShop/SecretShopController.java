/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

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
@RequestMapping("/secretShop")
public class SecretShopController implements ModuleApiProvider {

    @Autowired
    SecretShopService secretShopService;

    @RequestMapping("/get")
    public SecretShopOverall get(@AuthenticationPrincipal Account account) {
        return secretShopService.get(account.getId());
    }

    @RequestMapping("/getGrantingStats")
    public List<SecretShopPrizeGrantingStats> getGrantingStats(@AuthenticationPrincipal Account account) {
        return secretShopService.getGrantingStats();
    }

    @RequestMapping("/price")
    public List<Long> price(@AuthenticationPrincipal Account account) {
        return secretShopService.price(account.getId());
    }

    @PostMapping("/draw")
    public SecretShopOverall draw(
            @AuthenticationPrincipal Account account,
            @RequestParam("expectedPrice") long expectedPrice,
            @RequestParam("batchDraw") boolean batchDraw) {
        return secretShopService.draw(account.getId(), expectedPrice, batchDraw);
    }

    @RequestMapping("/take")
    public SecretShopOverall take(@AuthenticationPrincipal Account account) {
        return secretShopService.take(account.getId());
    }

    @PostMapping("/exchange")
    public SecretShopOverall exchange(@AuthenticationPrincipal Account account) {
        return secretShopService.exchange(account.getId());
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .name("神秘商店")
                .baseUri("/secretShop")
                //
                .webInterface()
                .uri("/get")
                .description("记录")
                .response(SecretShopOverall.class, "神秘商店记录")
                .and()
                //
                .webInterface()
                .uri("/getGrantingStats")
                .description("大奖产出记录")
                .responseArray(SecretShopPrizeGrantingStats.class, "大奖产出记录")
                .and()
                //
                .webInterface()
                .uri("/price")
                .description("价格")
                .responseArray(Long.class, "0号元素是兑换KC价格，1号元素是抽奖价格")
                .and()
                //
                .webInterface()
                .uri("/draw")
                .post()
                .description("抽奖")
                .requestParameter("long", "expectedPrice", "当前的货币消耗价")
                .requestParameter("boolean", "batchDraw", "是否批量抽奖")
                .response(SecretShopOverall.class, "神秘商店记录")
                .expectableError(SecretShopException.EC_HAVE_NOT_TAKEN_PRIZE, "有尚未领取的抽奖奖励")
                .and()
                //
                .webInterface()
                .uri("/take")
                .description("领奖")
                .response(SecretShopOverall.class, "神秘商店记录")
                .expectableError(SecretShopException.EC_NOT_HAVE_NOT_TAKEN_PRIZE, "没有尚未领取的抽奖奖励")
                .and()
                //
                .webInterface()
                .uri("/exchange")
                .post()
                .description("兑换")
                .response(SecretShopOverall.class, "神秘商店记录")
                .expectableError(SecretShopException.EC_INSUFFICIENT_KC_PACK, "没有可兑换的块币补给包")
                .and();
    }

}
