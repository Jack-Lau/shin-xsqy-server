/*
 * Created 2019-2-13 18:39:21
 */
package cn.com.yting.kxy.web.game.yuanxiaojiayao;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.controller.ControllerUtils;
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
@RequestMapping("/yuanxiaojiayao")
@ModuleDoc(moduleName = "元宵佳肴")
public class YxjyController {

    @Autowired
    private YxjyRepository yxjyRepository;

    @Autowired
    private YxjyService yxjyService;

    @RequestMapping("/viewMyself")
    @WebInterfaceDoc(description = "查询自己的记录", response = "元宵佳肴记录")
    public YxjyRecord viewMyself(@AuthenticationPrincipal Account account) {
        return yxjyRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/createRecord")
    @WebInterfaceDoc(description = "创建记录", response = "元宵佳肴记录")
    public YxjyRecord createRecord(@AuthenticationPrincipal Account account) {
        return yxjyService.createRecord(account.getId());
    }

    @PostMapping("/publishInvitation")
    @WebInterfaceDoc(description = "发出佳肴邀请", response = "元宵佳肴记录")
    public YxjyRecord publishInvitation(@AuthenticationPrincipal Account account) {
        return yxjyService.publishInvitation(account.getId());
    }

    @PostMapping("/attend")
    @WebInterfaceDoc(description = "接受邀请", response = "元宵佳肴记录")
    public YxjyRecord attend(
        @AuthenticationPrincipal Account account,
        @RequestParam("targetAccountId") @ParamDoc("玩家A的账号id") long targetAccountId
    ) {
        return yxjyService.attend(account.getId(), targetAccountId);
    }

    @PostMapping("/obtainAward")
    @WebInterfaceDoc(description = "开吃大餐", response = "元宵佳肴记录")
    public YxjyRecord obtainAward(@AuthenticationPrincipal Account account) {
        return yxjyService.obtainAward(account.getId());
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    @WebInterfaceDoc(description = "触发每日重置", response = "")
    public void triggerDailyReset() {
        yxjyService.dailyReset();
    }
}
