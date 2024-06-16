/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/treasureBowl")
@ModuleDoc(moduleName = "长乐聚宝盆")
public class TreasureBowlController {

    @Autowired
    TreasureBowlService treasureBowlService;

    @RequestMapping("/get")
    @WebInterfaceDoc(description = "查看当场记录", response = "聚合记录")
    public TreasureBowlOverall get(@AuthenticationPrincipal Account account) {
        return treasureBowlService.get(account.getId());
    }

    @RequestMapping("/today")
    @WebInterfaceDoc(description = "查看今日记录", response = "聚合记录")
    public List<TreasureBowl> today(@AuthenticationPrincipal Account account) {
        return treasureBowlService.today();
    }

    @PostMapping("/attend")
    @WebInterfaceDoc(description = "投入一个贡牌", response = "聚合记录")
    public TreasureBowlOverall attend(@AuthenticationPrincipal Account account) {
        return treasureBowlService.attend(account.getId());
    }

    @PostMapping("/take")
    @WebInterfaceDoc(description = "领取未领取的坊金", response = "聚合记录")
    public TreasureBowlOverall take(@AuthenticationPrincipal Account account) {
        return treasureBowlService.take(account.getId());
    }

}
