/*
 * Created 2018-9-4 18:36:31
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.captcha.TencentCaptchaApi;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.game.secretShop.SecretShopSharedRepository;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
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
@RequestMapping("/yibenwanli")
public class YibenwanliController implements ModuleApiProvider {

    @Autowired
    private YibenwanliRepository yibenwanliRepository;
    @Autowired
    private YibenwanliSharedRepository yibenwanliSharedRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SecretShopSharedRepository secretShopSharedRepository;

    @Autowired
    private YibenwanliService yibenwanliService;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private TencentCaptchaApi captchaApi;

    @RequestMapping("/overrall")
    public YibenwanliOverrall overrall() {
        YibenwanliSharedRecord sharedRecord = yibenwanliSharedRepository.getTheRecord();
        String lastPurchaserPlayerName = Optional.ofNullable(sharedRecord.getLastPurchaseAccountId())
                .map(id -> playerRepository.findById(id).orElse(null))
                .map(Player::getPlayerName)
                .orElse(null);
        Long timeToEnd = Optional.ofNullable(sharedRecord.getDeadlineTime())
                .map(it -> it.getTime() - timeProvider.currentTime())
                .orElse(null);
        Long timeToNextSeason = Optional.ofNullable(sharedRecord.getNextSeasonTime())
                .map(it -> it.getTime() - timeProvider.currentTime())
                .orElse(null);
        return new YibenwanliOverrall(
                sharedRecord.getPool(),
                sharedRecord.getTotalTicketCount(),
                lastPurchaserPlayerName,
                timeToEnd,
                sharedRecord.getTicketPrice(),
                sharedRecord.isClosed(),
                timeToNextSeason,
                sharedRecord.getLastShotRate(timeProvider.currentInstant())
        );
    }

    @RequestMapping("/view/myself")
    public YibenwanliRecord viewMyself(@AuthenticationPrincipal Account account) {
        return yibenwanliRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/purchase")
    public YibenwanliRecord purchase(
            @AuthenticationPrincipal Account account,
            @RequestParam("expectedPrice") long expectedPrice
    ) {
        return yibenwanliService.purchaseTicket(account.getId(), expectedPrice);
    }

    @DebugOnly
    @PostMapping("/tryPublishLastChangeBroadcast")
    public void tryPublishLastChangeBroadcast() {
        yibenwanliService.tryPublishLastChangeBroadcast();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("一本万利")
                .baseUri("/yibenwanli")
                //
                .webInterface()
                .uri("/overrall")
                .description("获得总览信息")
                .response(YibenwanliOverrall.class, "总览信息")
                .and()
                //
                .webInterface()
                .name("viewMyself")
                .uri("/view/myself")
                .description("获得自己的信息")
                .response(YibenwanliRecord.class, "自己的信息")
                .and()
                //
                .webInterface()
                .uri("/purchase")
                .post()
                .description("购买本票")
                .requestParameter("integer", "expectedPrice", "期望的购买价格")
                .response(YibenwanliRecord.class, "自己的信息")
                .and()
                //
                .webInterface()
                .uri("/tryPublishLastChangeBroadcast")
                .post()
                .description("尝试发送最后时间广播（测试用）")
                .and();
    }

}
