/*
 * Created 2018-11-8 11:47:36
 */
package cn.com.yting.kxy.web.title;

import java.util.List;

import cn.com.yting.kxy.core.DebugOnly;
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
@RequestMapping("/title")
public class TitleController implements ModuleApiProvider {

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private TitleService titleService;

    @RequestMapping("/view/{id}")
    public Title view(@PathVariable("id") long id) {
        return titleRepository.findById(id).orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/viewMine")
    public List<Title> viewMine(@AuthenticationPrincipal Account account) {
        return titleRepository.findByAccountId(account.getId());
    }

    @PostMapping("/action/{id}/primary")
    public WebMessageWrapper designatePrimaryTitle(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") long titleId
    ) {
        titleService.designatePrimaryTitle(account.getId(), titleId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/untitle")
    public WebMessageWrapper untitle(@AuthenticationPrincipal Account account) {
        titleService.untitle(account.getId());
        return WebMessageWrapper.ok();
    }

    @DebugOnly
    @PostMapping("/grantForTest")
    public Title grantForTest(
        @AuthenticationPrincipal Account account,
        @RequestParam("definitionId") long definitionId
    ) {
        return titleService.grantTitleForTest(account.getId(), definitionId);
    }

    @PostMapping("/redeem")
    public TitleRedeemResult redeemTitle(
        @AuthenticationPrincipal Account account,
        @RequestParam("currencyId") long currencyId
    ) {
        return titleService.redeemTitle(account.getId(), currencyId);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("title")
            .baseUri("/title")
            //
            .webInterface()
            .name("view")
            .uri("/view/{id}")
            .description("查询指定的称号")
            .requestParameter("number", "id", "称号的 id")
            .response(Title.class, "对应的称号")
            .and()
            //
            .webInterface()
            .uri("/viewMine")
            .description("查询自己所有的称号")
            .responseArray(Title.class, "称号的集合")
            .and()
            //
            .webInterface()
            .name("designatePrimaryTitle")
            .uri("/action/{id}/primary")
            .post()
            .description("设置（装着）一个主称号")
            .requestParameter("number", "id", "称号的 id")
            .and()
            //
            .webInterface()
            .uri("/untitle")
            .post()
            .description("取消（卸下）主称号")
            .and()
            //
            .webInterface()
            .uri("/grantForTest")
            .post()
            .description("生成一个称号（测试用）")
            .requestParameter("number", "definitionId", "称号的配置表定义的 id")
            .response(Title.class, "生成的称号")
            .and()
            //
            .webInterface()
            .uri("/redeem")
            .post()
            .description("兑换一个称号")
            .requestParameter("number", "currencyId", "用来兑换的货币 id")
            .response(TitleRedeemResult.class, "兑换结果")
            .and()
            ;
    }
}
