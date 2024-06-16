/*
 * Created 2018-7-7 18:34:41
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.captcha.TencentCaptchaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/kbdzp")
public class KbdzpController implements ModuleApiProvider {

    @Autowired
    private KbdzpRepository kbdzpRepository;

    @Autowired
    private KbdzpService kbdzpService;
    @Autowired
    private TencentCaptchaApi captchaApi;

    @RequestMapping("/view/myself")
    public KbdzpRecord viewMyself(@AuthenticationPrincipal Account account) {
        return kbdzpRepository.findById(account.getId()).get();
    }

    @RequestMapping("/view/fever")
    public boolean viewFever(@AuthenticationPrincipal Account account) {
        return kbdzpService.isFever();
    }

    @PostMapping("/recoverEnergy")
    public KbdzpRecoverResult recoverEnergy(@AuthenticationPrincipal Account account) {
        return kbdzpService.recoverEnergy(account.getId());
    }

    @PostMapping("/enableBooster1")
    public KbdzpRecord enableBooster1(
            @AuthenticationPrincipal Account account,
            @RequestParam("activationCode") String activationCode
    ) {
        return kbdzpService.enableBooster1(account.getId(), activationCode);
    }

    @PostMapping("/enableBooster2")
    public KbdzpRecord enableBooster2(
            @AuthenticationPrincipal Account account,
            @RequestParam("activationCode") String activationCode
    ) {
        return kbdzpService.enableBooster2(account.getId(), activationCode);
    }

    @PostMapping("/makeTurn")
    public KbdzpRecord makeTurn(
        @AuthenticationPrincipal Account account,
        @RequestParam(name = "ticket", required = false) @ParamDoc("ticket") String ticket,
        @RequestParam(name = "randStr", required = false) @ParamDoc("randStr") String randStr,
        HttpServletRequest request
    ) {
        KbdzpRecord kbdzpRecord = kbdzpRepository.findById(account.getId()).orElse(null);
        if (kbdzpRecord != null && (kbdzpRecord.getTodayTurnCount() == 10 || kbdzpRecord.getTodayTurnCount() == 20)) {
            if (!captchaApi.verify(ticket, randStr, request.getRemoteAddr())) {
                throw KxyWebException.unknown("验证失败");
            }
        }
        return kbdzpService.makeTurn(account.getId());
    }

    @PostMapping("/obtainAward")
    public KbdzpRecord obtainAward(@AuthenticationPrincipal Account account) {
        return kbdzpService.obtainAward(account.getId());
    }

    @RequestMapping("/latestInterestingAwards")
    public Collection<KbdzpAwardLog> latestInterestingAwards() {
        return kbdzpService.getLatestInterestingAwards();
    }

    @PostMapping("/obtainInviteeBonus")
    public KbdzpRecord obtainInviteeBonus(@AuthenticationPrincipal Account account) {
        return kbdzpService.obtainInviteeBonus(account.getId());
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("块币大转盘")
                .baseUri("/kbdzp")
                //
                .webInterface()
                .name("viewMyself")
                .uri("/view/myself")
                .description("查看自己的块币大转盘记录")
                .response(KbdzpRecord.class, "转盘记录")
                .and()
                //
                .webInterface()
                .name("viewFever")
                .uri("/view/fever")
                .description("查看现在转盘是否为周末fever状态")
                .response(Boolean.class, "是或否")
                .and()
                //
                .webInterface()
                .uri("/recoverEnergy")
                .post()
                .description("结算一次块币大转盘能量回复")
                .response(KbdzpRecoverResult.class, "转盘回复结果")
                .and()
                //
                .webInterface()
                .uri("/enableBooster1")
                .post()
                .description("激活能量回复奖励1")
                .requestParameter("string", "activationCode", "激活码")
                .response(KbdzpRecord.class, "转盘记录")
                .expectableError(KbdzpException.EC_BOOSTER_ALREADY_ENABLED, "指定的激活码已经使用过")
                .expectableError(KbdzpException.EC_ACODE_NOT_VALID, "激活码不正确")
                .and()
                //
                .webInterface()
                .uri("/enableBooster2")
                .post()
                .description("激活能量回复奖励2")
                .requestParameter("string", "activationCode", "激活码")
                .response(KbdzpRecord.class, "转盘记录")
                .expectableError(KbdzpException.EC_BOOSTER_ALREADY_ENABLED, "指定的激活码已经使用过")
                .expectableError(KbdzpException.EC_ACODE_NOT_VALID, "激活码不正确")
                .and()
                //
                .webInterface()
                .uri("/makeTurn")
                .post()
                .description("转一下转盘")
                .response(KbdzpRecord.class, "转盘记录")
                .expectableError(KbdzpException.EC_INSUFFICIENT_ENERGY, "能量值不足")
                .expectableError(KbdzpException.EC_PENDING_AWARD_EXISTED, "当前存在未领取的奖励")
                .and()
                //
                .webInterface()
                .uri("/obtainAward")
                .post()
                .description("领取转盘奖励")
                .response(KbdzpRecord.class, "转盘记录")
                .expectableError(KbdzpException.EC_PENDING_AWARD_NOT_EXISTED, "当前不存在未领取的奖励")
                .and()
                //
                .webInterface()
                .uri("/latestInterestingAwards")
                .description("获得最近的需要展示的奖励记录")
                .responseArray(KbdzpAwardLog.class, "转盘奖励记录")
                .and()
                //
                .webInterface()
                .uri("/obtainInviteeBonus")
                .post()
                .description("领取被邀请奖励")
                .response(KbdzpRecord.class, "转盘记录")
                .expectableError(KbdzpException.EC_INVITEE_BONUS_NOT_AVAILABLE, "邀请奖励不可用")
                .and();
    }

}
