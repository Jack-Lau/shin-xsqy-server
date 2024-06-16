/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/fishing")
@ModuleDoc(moduleName = "钓鱼")
public class FishingController {

    @Autowired
    FishingService fishingService;

    @RequestMapping("/get")
    @WebInterfaceDoc(description = "查看钓鱼记录", response = "聚合记录")
    public FishingOverall get(@AuthenticationPrincipal Account account) {
        return fishingService.get(account.getId());
    }

    @PostMapping("/buy")
    @WebInterfaceDoc(description = "购买钓竿", response = "聚合记录")
    public FishingOverall buy(@AuthenticationPrincipal Account account) {
        return fishingService.buy(account.getId());
    }

    @PostMapping("/fish")
    @WebInterfaceDoc(description = "开始一次钓鱼", response = "聚合记录")
    public FishingOverall fish(@AuthenticationPrincipal Account account) {
        return fishingService.fish(account.getId());
    }

    @PostMapping("/finish")
    @WebInterfaceDoc(description = "结束一次钓鱼", response = "聚合记录")
    public FishingOverall finish(
            @AuthenticationPrincipal Account account,
            @RequestParam("fishingOnceRecordId") @ParamDoc("一次钓鱼记录的id") long fishingOnceRecordId
    ) {
        return fishingService.finish(account.getId(), fishingOnceRecordId);
    }

}
