/*
 * Created 2018-7-27 11:28:41
 */
package cn.com.yting.kxy.web.gift;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/gift")
public class GiftController implements ModuleApiProvider {

    @Autowired
    private GiftService giftService;

    @PostMapping("/redeem")
    public WebMessageWrapper redeem(
        @AuthenticationPrincipal Account account,
        @RequestParam("code") String code
    ) {
        giftService.redeem(account.getId(), code);
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("gift")
            .baseUri("/gift")
            //
            .webInterface()
            .name("redeem")
            .uri("/redeem")
            .post()
            .description("使用兑换码兑换礼包")
            .requestParameter("string", "code", "兑换码")
            .expectableError(GiftException.EC_GIFT_NOT_FOUND, "兑换码对应的礼包不存在")
            .expectableError(GiftException.EC_GIFT_REDEEMED, "礼包已被兑换")
            .expectableError(GiftException.EC_GIFT_NOT_AVAILABLE, "礼包尚不可用")
            .expectableError(GiftException.EC_OVER_LIMITATION, "礼包兑换超过次数限制")
            .and();
    }
}
