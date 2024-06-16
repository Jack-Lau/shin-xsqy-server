/*
 * Created 2019-1-23 15:45:10
 */
package cn.com.yting.kxy.web.game.zaixianjiangli;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.award.AwardResult;
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
@RequestMapping("/zxjl")
@ModuleDoc(moduleName = "在线奖励")
public class ZxjlController {

    @Autowired
    private ZxjlService zxjlService;

    @RequestMapping("/myself")
    @WebInterfaceDoc(description = "查询自己的记录", response = "在线奖励记录")
    public ZxjlRecord viewMyself(@AuthenticationPrincipal Account account) {
        return zxjlService.findOrCreateRecord(account.getId());
    }

    @PostMapping("/obtainAward")
    @WebInterfaceDoc(description = "获取奖励", response = "奖励结果")
    public AwardResult obtainAward(
        @AuthenticationPrincipal Account account,
        @RequestParam("index") @ParamDoc("要领取的奖励的索引") int index
    ) {
        return zxjlService.obtainAward(account.getId(), index);
    }
}
