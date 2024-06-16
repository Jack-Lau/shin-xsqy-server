/*
 * Created 2018-8-11 17:35:13
 */
package cn.com.yting.kxy.web.award;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/award")
public class AwardController implements ModuleApiProvider {

    @Autowired
    private WebsocketMessageService websocketMessageService;
    @Autowired
    private AwardService awardService;

    @PostMapping("/redeem")
    public AwardResult redeem(
        @AuthenticationPrincipal Account account,
        @RequestParam("currencyId") long currencyId
    ) {
        return awardService.redeemAward(account.getId(), currencyId);
    }

    @TransactionalEventListener
    public void onAward(AwardEvent event) {
        websocketMessageService.sendToUser(event.getAccountId(), "/award/award", event.getResult());
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("award")
            .baseUri("/award")
            //
            .webInterface()
            .uri("/redeem")
            .post()
            .description("用货币兑换一个奖励")
            .requestParameter("number", "currencyId", "要兑换的货币id")
            .response(AwardResult.class, "奖励结果")
            .and()
            //
            //
            //
            .webNotification()
            .queue("/award/award")
            .description("获得奖励的通知")
            .messageType(AwardResult.class)
            .and();
    }

}
