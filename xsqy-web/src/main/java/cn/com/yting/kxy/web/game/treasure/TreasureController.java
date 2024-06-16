/*
 * Created 2018-11-2 17:35:21
 */
package cn.com.yting.kxy.web.game.treasure;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/treasure")
public class TreasureController implements ModuleApiProvider {

    @Autowired
    private TreasureService treasureService;

    @PostMapping("/obtainTreasure")
    public CurrencyStack obtainTreasure(@AuthenticationPrincipal Account account) {
        return treasureService.obtainTreasure(account.getId());
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("treasure")
            .baseUri("/treasure")
            //
            .webInterface()
            .uri("/obtainTreasure")
            .post()
            .description("消耗藏宝图获得奖励")
            .response(CurrencyStack.class, "奖励的货币")
            .and();
    }
}
